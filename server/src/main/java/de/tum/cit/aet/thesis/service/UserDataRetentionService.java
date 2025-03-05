package de.tum.cit.aet.thesis.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service responsible for managing user data retention in compliance with GDPR.
 * Handles identification and deletion of user data older than the configured retention period.
 */
@Service
@Slf4j
public class UserDataRetentionService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UploadService uploadService;

    @Value("${data-retention.user-data-years:10}")
    private int userDataRetentionYears;

    @Value("${data-retention.batch-size:100}")
    private int batchSize;

    @Value("${thesis-management.storage.upload-location:uploads}")
    private String uploadLocation;

    @Autowired
    public UserDataRetentionService(
            UserRepository userRepository,
            UserGroupRepository userGroupRepository,
            UploadService uploadService) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.uploadService = uploadService;
    }

    /**
     * Scheduled task to clean up user data that exceeds the retention period.
     * Runs according to the configured schedule (default: daily at 2 AM).
     */
    @Scheduled(cron = "${data-retention.schedule:0 0 2 * * *}")
    public void scheduleUserDataCleanup() {
        log.info("Starting scheduled user data cleanup task");
        try {
            int processedCount = processUsersForDeletion();
            log.info("Completed user data cleanup task. Processed {} users", processedCount);
        } catch (Exception e) {
            log.error("Error during scheduled user data cleanup", e);
        }
    }

    /**
     * Identifies and processes users eligible for data deletion based on retention policy.
     * 
     * @return the number of users processed
     */
    @Transactional
    public int processUsersForDeletion() {
        Instant cutoffDate = Instant.now().minus(userDataRetentionYears, ChronoUnit.YEARS);
        log.info("Processing users joined before {}", cutoffDate);
        
        // Use the repository method to efficiently find eligible users
        List<User> usersToProcess = userRepository.findUsersInactiveOlderThan(cutoffDate);
        log.info("Found {} users eligible for data deletion", usersToProcess.size());
        
        AtomicInteger processedCount = new AtomicInteger(0);
        
        // Process users in batches to avoid overwhelming the system
        for (int i = 0; i < usersToProcess.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, usersToProcess.size());
            List<User> batch = usersToProcess.subList(i, endIndex);
            
            batch.forEach(user -> {
                try {
                    anonymizeUserData(user);
                    processedCount.incrementAndGet();
                    log.info("Successfully anonymized user with ID: {}", user.getId());
                } catch (Exception e) {
                    log.error("Failed to anonymize user with ID: {}", user.getId(), e);
                }
            });
        }
        
        return processedCount.get();
    }

    /**
     * Anonymizes a user's personal data while preserving academic records.
     * 
     * @param user the user to anonymize
     */
    @Transactional
    public void anonymizeUserData(User user) {
        UUID userId = user.getId();
        log.info("Anonymizing user data for user ID: {}", userId);
        
        // Delete user files
        deleteUserFiles(user);
        
        // Anonymize personal data
        user.setEmail("anonymized-" + userId + "@deleted.user");
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setGender(null);
        user.setNationality(null);
        user.setMatriculationNumber(null);
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setAvatar(null);
        user.setProjects(null);
        user.setInterests(null);
        user.setSpecialSkills(null);
        user.setCustomData(new HashMap<>());
        
        // Mark as anonymized in custom data
        Map<String, String> metadata = new HashMap<>();
        metadata.put("anonymized", "true");
        metadata.put("anonymizedAt", Instant.now().toString());
        user.setCustomData(metadata);
        
        // Save anonymized user
        userRepository.save(user);
        
        // Note: We're keeping the user record and their academic data (theses, applications) 
        // but with personal information removed for historical/statistical purposes
        log.info("User data anonymized successfully for user ID: {}", userId);
    }

    /**
     * Deletes all files associated with a user.
     * 
     * @param user the user whose files should be deleted
     */
    private void deleteUserFiles(User user) {
        try {
            // Delete CV file if exists
            if (user.getCvFilename() != null && !user.getCvFilename().isEmpty()) {
                deleteFile(user.getCvFilename());
                log.info("Deleted CV file for user ID: {}", user.getId());
            }
            
            // Delete degree file if exists
            if (user.getDegreeFilename() != null && !user.getDegreeFilename().isEmpty()) {
                deleteFile(user.getDegreeFilename());
                log.info("Deleted degree file for user ID: {}", user.getId());
            }
            
            // Delete examination file if exists
            if (user.getExaminationFilename() != null && !user.getExaminationFilename().isEmpty()) {
                deleteFile(user.getExaminationFilename());
                log.info("Deleted examination file for user ID: {}", user.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting files for user ID: {}", user.getId(), e);
        }
    }

    /**
     * Deletes a file from the file system.
     * 
     * @param filename the name of the file to delete
     */
    private void deleteFile(String filename) {
        try {
            if (filename != null && !filename.isEmpty()) {
                // First try using the upload service to access the file
                try {
                    Resource resource = uploadService.load(filename);
                    try {
                        // Get the file path and delete it if it exists
                        Path filePath = Paths.get(resource.getURI());
                        if (Files.deleteIfExists(filePath)) {
                            log.info("Successfully deleted file: {}", filename);
                            return;
                        }
                    } catch (IOException e) {
                        log.warn("Could not delete file via URI: {}, trying direct path", filename);
                    }
                } catch (Exception e) {
                    log.warn("Could not access file via UploadService: {}, trying direct path", filename);
                }
                
                // Fallback to direct file access if resource approach fails
                Path directPath = Paths.get(uploadLocation, filename);
                if (Files.deleteIfExists(directPath)) {
                    log.info("Successfully deleted file via direct path: {}", filename);
                } else {
                    log.warn("File not found at expected location: {}", directPath);
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete file: {}", filename, e);
        }
    }
}
