package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.config.DataRetentionConfig;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.entity.ThesisRole;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing user data retention in compliance with GDPR.
 * Handles identifying and anonymizing/deleting user data older than the configured retention period.
 * 
 * This service implements automated data removal for users whose data is older than the
 * configured retention period (default: 10 years). In accordance with GDPR Article 17
 * (Right to erasure/'right to be forgotten'), this service:
 * 
 * 1. Identifies users with no activity for the specified period
 * 2. Deletes personal identifiable information (PII)
 * 3. Anonymizes academic records while preserving system integrity
 * 4. Maintains audit logs of the anonymization process
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataRetentionService {

    // SQL query to identify users whose data exceeds retention period
    private static final String FIND_USERS_QUERY = 
            "SELECT u.* FROM users u " +
            "WHERE u.joined_at < :cutoffDate " +
            "AND u.updated_at < :cutoffDate";
    
    // SQL query to anonymize applications
    private static final String ANONYMIZE_APPLICATIONS_QUERY = 
            "UPDATE applications SET " +
            "student_comment = 'Anonymized due to GDPR compliance', " +
            "advisor_comment = CASE WHEN advisor_comment IS NOT NULL THEN advisor_comment ELSE NULL END, " +
            "supervisor_comment = CASE WHEN supervisor_comment IS NOT NULL THEN supervisor_comment ELSE NULL END " +
            "WHERE user_id = :userId";
    
    // SQL query to anonymize thesis roles
    private static final String ANONYMIZE_THESIS_ROLES_QUERY = 
            "UPDATE thesis_roles SET " +
            "comment = 'Anonymized due to GDPR compliance' " +
            "WHERE user_id = :userId AND comment IS NOT NULL";
    
    // SQL query to anonymize thesis comments
    private static final String ANONYMIZE_THESIS_COMMENTS_QUERY = 
            "UPDATE thesis_comments SET " +
            "content = 'Anonymized due to GDPR compliance' " +
            "WHERE creator_id = :userId";

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ThesisRepository thesisRepository;
    private final ThesisRoleRepository thesisRoleRepository;
    private final DataRetentionConfig config;
    private final UploadService uploadService;
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Scheduled task to run the data retention process based on the configured schedule.
     * Identifies and processes users whose data is older than the retention period.
     */
    @Scheduled(cron = "#{@dataRetentionConfig.schedule}")
    public void runDataRetentionJob() {
        log.info("Starting scheduled data retention job");
        try {
            List<User> usersToProcess = identifyUsersForDeletion();
            log.info("Found {} users that exceed data retention period of {} years", 
                    usersToProcess.size(), config.getUserDataYears());
            
            processUsersInBatches(usersToProcess);
            
            log.info("Data retention job completed successfully");
        } catch (Exception e) {
            log.error("Error during data retention job execution", e);
        }
    }
    
    /**
     * Process users in batches to prevent memory issues with large datasets
     *
     * @param usersToProcess List of users to be processed
     */
    private void processUsersInBatches(List<User> usersToProcess) {
        int totalBatches = (int) Math.ceil((double) usersToProcess.size() / config.getBatchSize());
        
        for (int i = 0; i < usersToProcess.size(); i += config.getBatchSize()) {
            int endIndex = Math.min(i + config.getBatchSize(), usersToProcess.size());
            List<User> batch = usersToProcess.subList(i, endIndex);
            
            int currentBatch = (i / config.getBatchSize()) + 1;
            log.info("Processing batch {} of {}", currentBatch, totalBatches);
            
            batch.forEach(this::anonymizeUserData);
            
            log.info("Completed batch {} of {}", currentBatch, totalBatches);
        }
    }

    /**
     * Identifies users whose data is older than the configured retention period.
     * Users are eligible for data deletion/anonymization if:
     * 1. They joined more than X years ago (X defined in configuration)
     * 2. They have no recent activity (based on updated_at field)
     *
     * @return List of users eligible for data deletion/anonymization
     */
    @Transactional(readOnly = true)
    public List<User> identifyUsersForDeletion() {
        Instant cutoffDate = Instant.now().minus(config.getUserDataYears(), ChronoUnit.YEARS);
        
        log.debug("Identifying users who joined before {}", cutoffDate);
        
        // Use native query for better performance with large datasets
        Query query = entityManager.createNativeQuery(FIND_USERS_QUERY, User.class)
                .setParameter("cutoffDate", cutoffDate);
                
        @SuppressWarnings("unchecked")
        List<User> users = query.getResultList();
        
        return users;
    }

    /**
     * Anonymizes or deletes a user's personal data in compliance with GDPR.
     * Personal identifiable information is removed, while academic records are anonymized.
     *
     * @param user The user whose data should be anonymized/deleted
     */
    @Transactional
    public void anonymizeUserData(User user) {
        UUID userId = user.getId();
        log.info("Anonymizing data for user ID: {}", userId);
        
        try {
            // Save original universityId for logging
            String originalUniversityId = user.getUniversityId();
            
            // 1. Delete personal files
            deleteUserFiles(user);
            
            // 2. Anonymize user record
            anonymizeUserRecord(user);
            
            // 3. Anonymize user applications
            anonymizeUserApplications(userId);
            
            // 4. Anonymize thesis roles
            anonymizeThesisRoles(userId);
            
            // 5. Log the deletion for audit purposes
            logUserDeletion(originalUniversityId);
            
            log.info("Successfully anonymized data for user ID: {}", userId);
        } catch (Exception e) {
            log.error("Error anonymizing data for user ID: {}", userId, e);
            throw e; // Rethrow to trigger transaction rollback
        }
    }
    
    /**
     * Deletes user-related files (CV, degree documents, etc.)
     * 
     * @param user User whose files should be deleted
     */
    private void deleteUserFiles(User user) {
        // Delete CV file
        deleteFileIfExists(user.getCvFilename(), "CV", user.getId());
        
        // Delete degree file
        deleteFileIfExists(user.getDegreeFilename(), "degree", user.getId());
        
        // Delete examination file
        deleteFileIfExists(user.getExaminationFilename(), "examination", user.getId());
    }
    
    /**
     * Helper method to delete a file if it exists
     * 
     * @param filename The name of the file to delete
     * @param fileType Human-readable file type for logging
     * @param userId User ID for logging
     */
    private void deleteFileIfExists(String filename, String fileType, UUID userId) {
        if (filename != null && !filename.isEmpty()) {
            try {
                uploadService.deleteFile(filename);
                log.debug("Deleted {} file for user ID: {}", fileType, userId);
            } catch (Exception e) {
                log.warn("Failed to delete {} file for user ID: {}", fileType, userId, e);
            }
        }
    }
    
    /**
     * Anonymizes the user record by removing or replacing personal identifiable information
     * 
     * @param user User to anonymize
     */
    private void anonymizeUserRecord(User user) {
        String anonymizedId = "anonymized-" + user.getId().toString();
        
        // Anonymize personal data
        user.setEmail(null);
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setGender(null);
        user.setNationality(null);
        user.setMatriculationNumber(null);
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setAvatar(null);
        
        // Clear custom data
        user.setCustomData(null);
        
        // Mark as anonymized in the universityId field for tracking
        user.setUniversityId(anonymizedId);
        
        // Save the anonymized user
        userRepository.save(user);
        
        log.debug("Anonymized user record for ID: {}", user.getId());
    }
    
    /**
     * Anonymizes applications associated with the user
     * 
     * @param userId ID of the user whose applications should be anonymized
     */
    private void anonymizeUserApplications(UUID userId) {
        int updated = entityManager.createNativeQuery(ANONYMIZE_APPLICATIONS_QUERY)
                .setParameter("userId", userId)
                .executeUpdate();
                
        log.debug("Anonymized {} applications for user ID: {}", updated, userId);
    }
    
    /**
     * Anonymizes thesis roles - keep the role assignment but remove any personal comments
     * 
     * @param userId ID of the user whose thesis roles should be anonymized
     */
    private void anonymizeThesisRoles(UUID userId) {
        // Anonymize thesis roles
        int rolesUpdated = entityManager.createNativeQuery(ANONYMIZE_THESIS_ROLES_QUERY)
                .setParameter("userId", userId)
                .executeUpdate();
                
        log.debug("Anonymized {} thesis roles for user ID: {}", rolesUpdated, userId);
        
        // Anonymize thesis comments
        int commentsUpdated = entityManager.createNativeQuery(ANONYMIZE_THESIS_COMMENTS_QUERY)
                .setParameter("userId", userId)
                .executeUpdate();
                
        log.debug("Anonymized {} thesis comments for user ID: {}", commentsUpdated, userId);
    }
    
    /**
     * Logs user deletion for audit and compliance purposes
     * 
     * @param universityId Original university ID of the anonymized user
     */
    private void logUserDeletion(String universityId) {
        log.info("GDPR_COMPLIANCE_LOG: User data anonymized for university ID '{}' at {}", 
                universityId, Instant.now());
        
        // Additional logging could be implemented here, such as:
        // - Writing to a dedicated database table
        // - Sending a notification to compliance team
        // - Creating an audit log file
    }
}
