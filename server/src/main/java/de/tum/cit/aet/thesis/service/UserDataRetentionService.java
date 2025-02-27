package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ThesisCommentRepository;
import de.tum.cit.aet.thesis.repository.ThesisPresentationRepository;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing user data retention in compliance with GDPR.
 * Handles identification and deletion/anonymization of user data older than the configured retention period.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataRetentionService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final ApplicationRepository applicationRepository;
    private final ThesisRepository thesisRepository;
    private final ThesisRoleRepository thesisRoleRepository;
    private final ThesisCommentRepository thesisCommentRepository;
    private final ThesisPresentationRepository thesisPresentationRepository;
    private final UploadService uploadService;

    @Value("${data-retention.user-data-years:10}")
    private int userDataRetentionYears;

    @Value("${data-retention.batch-size:100}")
    private int batchSize;

    /**
     * Scheduled task to identify and process users whose data should be deleted or anonymized.
     * Runs at the configured schedule (default: 2 AM daily).
     */
    @Scheduled(cron = "${data-retention.schedule:0 0 2 * * *}")
    public void processDataRetention() {
        log.info("Starting scheduled user data retention process");
        try {
            List<User> usersToProcess = identifyUsersForDeletion(batchSize);
            log.info("Found {} users for GDPR data processing", usersToProcess.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (User user : usersToProcess) {
                try {
                    anonymizeUserData(user);
                    successCount++;
                    log.info("Successfully processed user data for GDPR compliance: {}", user.getId());
                } catch (Exception e) {
                    failureCount++;
                    log.error("Error processing user {} for GDPR compliance: {}", user.getId(), e.getMessage(), e);
                }
            }
            
            log.info("Data retention process completed. Success: {}, Failures: {}", successCount, failureCount);
        } catch (Exception e) {
            log.error("Error in scheduled data retention process: {}", e.getMessage(), e);
        }
    }

    /**
     * Identifies users whose data is eligible for deletion/anonymization based on retention policy.
     * 
     * @param limit Maximum number of users to return
     * @return List of users eligible for data processing
     */
    public List<User> identifyUsersForDeletion(int limit) {
        Instant cutoffDate = Instant.now().minus(userDataRetentionYears, ChronoUnit.YEARS);
        log.debug("Identifying users with no activity since {}", cutoffDate);
        return userRepository.findUsersWithNoRecentActivity(cutoffDate, limit);
    }

    /**
     * Anonymizes or deletes user data in compliance with GDPR requirements.
     * Uses a transaction to ensure all operations succeed or fail together.
     * 
     * @param user The user whose data should be processed
     */
    @Transactional
    public void anonymizeUserData(User user) {
        UUID userId = user.getId();
        log.info("Starting anonymization/deletion for user: {}", userId);

        // Delete personal files
        deleteUserFiles(user);

        // Anonymize academic records and references
        anonymizeAcademicRecords(userId);

        // Delete personal data (MUST DELETE as per GDPR)
        anonymizePersonalData(user);

        // Save changes
        userRepository.save(user);
        log.info("Completed anonymization/deletion for user: {}", userId);
    }

    /**
     * Deletes user files (CV, degree, examination) from storage
     * Each file deletion is handled separately to ensure partial success if some files cannot be deleted
     */
    private void deleteUserFiles(User user) {
        // Create a copy of filenames before setting them to null
        String cvFile = user.getCvFilename();
        String degreeFile = user.getDegreeFilename();
        String examFile = user.getExaminationFilename();
        
        boolean hasDeletedFiles = false;

        // Delete CV file if exists
        if (cvFile != null && !cvFile.isEmpty()) {
            try {
                uploadService.deleteFile(cvFile);
                hasDeletedFiles = true;
                log.info("Deleted CV file for user {}: {}", user.getId(), cvFile);
            } catch (Exception e) {
                log.warn("Could not delete CV file for user {}: {}", user.getId(), e.getMessage());
            }
        }

        // Delete degree file if exists
        if (degreeFile != null && !degreeFile.isEmpty()) {
            try {
                uploadService.deleteFile(degreeFile);
                hasDeletedFiles = true;
                log.info("Deleted degree file for user {}: {}", user.getId(), degreeFile);
            } catch (Exception e) {
                log.warn("Could not delete degree file for user {}: {}", user.getId(), e.getMessage());
            }
        }

        // Delete examination file if exists
        if (examFile != null && !examFile.isEmpty()) {
            try {
                uploadService.deleteFile(examFile);
                hasDeletedFiles = true;
                log.info("Deleted examination file for user {}: {}", user.getId(), examFile);
            } catch (Exception e) {
                log.warn("Could not delete examination file for user {}: {}", user.getId(), e.getMessage());
            }
        }
        
        if (!hasDeletedFiles) {
            log.info("No files to delete for user {}", user.getId());
        }
    }

    /**
     * Anonymizes personal data for a user while preserving system references
     * This is compliant with GDPR Article 17 which requires erasure of personal data
     */
    private void anonymizePersonalData(User user) {
        log.debug("Anonymizing personal data for user {}", user.getId());
        
        // Keep ID and universityId for reference but anonymize all personal data
        user.setEmail("anonymized-" + user.getId() + "@deleted.gdpr");
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
        
        // Set fields that track user activity
        user.setUpdatedAt(Instant.now());
    }

    /**
     * Anonymizes academic records related to the user
     * Academic records are preserved for historical and statistical purposes (GDPR Article 89)
     * but any personal information within them is anonymized
     */
    private void anonymizeAcademicRecords(UUID userId) {
        int commentCount = thesisCommentRepository.anonymizeUserComments(userId);
        log.info("Anonymized {} thesis comments for user: {}", commentCount, userId);

        int applicationCount = applicationRepository.markApplicationsAnonymized(userId);
        log.info("Marked {} applications as anonymized for user: {}", applicationCount, userId);

        int presentationCount = thesisPresentationRepository.markPresentationsAnonymized(userId);
        log.info("Marked {} presentations as anonymized for user: {}", presentationCount, userId);
    }
}
