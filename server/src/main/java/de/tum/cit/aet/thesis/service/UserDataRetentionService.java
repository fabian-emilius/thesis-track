package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.config.DataRetentionConfig;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing user data retention in compliance with GDPR requirements.
 * Handles the identification and anonymization of inactive user data older than the configured retention period.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataRetentionService {

    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final DataRetentionConfig config;

    @PostConstruct
    public void validateConfig() {
        Assert.isTrue(config.getUserDataYears() > 0, "Data retention period must be positive");
        Assert.isTrue(config.getBatchSize() > 0, "Batch size must be positive");
        log.info("Data retention configuration validated - Period: {} years, Batch size: {}",
                config.getUserDataYears(), config.getBatchSize());
    }

    /**
     * Scheduled task that processes user data retention.
     * Identifies and anonymizes user data that exceeds the retention period.
     * Processes users in batches to manage memory usage and system load.
     */
    @Scheduled(cron = "#{@dataRetentionConfig.schedule}")
    public void processDataRetention() {
        log.info("Starting scheduled user data retention process");
        try {
            List<User> usersToProcess = findUsersForDeletion();
            log.info("Found {} users for data retention processing", usersToProcess.size());

            int totalBatches = (usersToProcess.size() + config.getBatchSize() - 1) / config.getBatchSize();
            int processedUsers = 0;
            int failedUsers = 0;

            for (int i = 0; i < usersToProcess.size(); i += config.getBatchSize()) {
                int endIndex = Math.min(i + config.getBatchSize(), usersToProcess.size());
                List<User> batch = usersToProcess.subList(i, endIndex);
                
                try {
                    processBatch(batch);
                    processedUsers += batch.size();
                } catch (Exception e) {
                    failedUsers += batch.size();
                    log.error("Failed to process batch {}/{}", (i / config.getBatchSize()) + 1, totalBatches, e);
                }

                log.info("Processed batch {}/{} ({} users)", 
                    (i / config.getBatchSize()) + 1,
                    totalBatches,
                    batch.size());
            }

            log.info("Completed user data retention process - Successfully processed: {}, Failed: {}", 
                processedUsers, failedUsers);
        } catch (Exception e) {
            log.error("Error during data retention process", e);
            throw new RuntimeException("Data retention process failed", e);
        }
    }

    /**
     * Identifies users whose data exceeds the retention period and have no recent activity.
     *
     * @return List of users eligible for data anonymization
     */
    @Transactional(readOnly = true)
    public List<User> findUsersForDeletion() {
        try {
            Instant cutoffDate = Instant.now().minus(config.getUserDataYears(), ChronoUnit.YEARS);
            log.debug("Finding users for deletion with cutoff date: {}", cutoffDate);
            return userRepository.findUsersForDeletion(cutoffDate);
        } catch (DataAccessException e) {
            log.error("Database error while finding users for deletion", e);
            throw new RuntimeException("Failed to query users for deletion", e);
        }
    }

    /**
     * Processes a batch of users for data anonymization.
     * Handles each user individually within a transaction.
     *
     * @param users List of users to process
     */
    protected void processBatch(List<User> users) {
        for (User user : users) {
            try {
                anonymizeUserData(user);
                log.info("Successfully processed user: {}", user.getId());
            } catch (Exception e) {
                log.error("Error processing user: {}", user.getId(), e);
                throw new RuntimeException("Failed to process user: " + user.getId(), e);
            }
        }
    }

    /**
     * Anonymizes personal data for a specific user while preserving academic records.
     * Deletes associated files and updates the user record with anonymized data.
     *
     * @param user The user whose data should be anonymized
     */
    @Transactional
    protected void anonymizeUserData(User user) {
        UUID userId = user.getId();
        log.info("Starting anonymization for user: {}", userId);

        try {
            // Delete personal files first
            deleteUserFiles(user);

            // Anonymize personal data
            anonymizePersonalData(user);

            // Anonymize academic data while preserving records
            anonymizeAcademicData(user);

            userRepository.save(user);
            log.info("Completed anonymization for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to anonymize user: {}", userId, e);
            throw new RuntimeException("User anonymization failed", e);
        }
    }

    /**
     * Deletes all personal files associated with a user.
     *
     * @param user The user whose files should be deleted
     */
    private void deleteUserFiles(User user) {
        try {
            if (user.getCvFilename() != null) {
                uploadService.deleteFile(user.getCvFilename());
                log.debug("Deleted CV file for user: {}", user.getId());
            }
            if (user.getDegreeFilename() != null) {
                uploadService.deleteFile(user.getDegreeFilename());
                log.debug("Deleted degree file for user: {}", user.getId());
            }
            if (user.getExaminationFilename() != null) {
                uploadService.deleteFile(user.getExaminationFilename());
                log.debug("Deleted examination file for user: {}", user.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting files for user: {}", user.getId(), e);
            throw new RuntimeException("File deletion failed", e);
        }
    }

    /**
     * Anonymizes personal information for a user.
     *
     * @param user The user whose personal data should be anonymized
     */
    private void anonymizePersonalData(User user) {
        user.setEmail("anonymized_" + user.getId() + "@deleted.user");
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setGender(null);
        user.setNationality(null);
        user.setMatriculationNumber(null);
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setAvatar(null);
        user.setCustomData(null);
    }

    /**
     * Anonymizes academic information while preserving necessary records.
     *
     * @param user The user whose academic data should be anonymized
     */
    private void anonymizeAcademicData(User user) {
        user.setProjects("[Anonymized]");
        user.setInterests("[Anonymized]");
        user.setSpecialSkills("[Anonymized]");
    }
}
