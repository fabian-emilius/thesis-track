package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.exception.DataRetentionException;
import de.tum.cit.aet.thesis.service.audit.DataRetentionAuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for implementing GDPR data retention policies.
 * Handles automated deletion of personal data for inactive users after 10 years
 * of inactivity, in compliance with data protection regulations.
 *
 * Key responsibilities:
 * - Identifying users eligible for data deletion based on inactivity
 * - Safely removing personal data while maintaining system integrity
 * - Logging deletion operations for audit purposes
 * - Managing batch processing of deletions
 *
 * @see User
 * @see UploadService
 * @see DataRetentionAuditService
 * @see DataRetentionException
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class DataRetentionService {
    // Constants for data retention policy
    private static final int BATCH_SIZE = 100;
    private static final int RETENTION_YEARS = 10;
    public static final String DELETED_MARKER = "[DELETED]";
    public static final String DELETION_REASON = "Retention policy - 10 years inactivity";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final UserRepository userRepository;
    private final UploadService uploadService;
    private final DataRetentionAuditService auditService;
    
    @Autowired
    public DataRetentionService(
            UserRepository userRepository,
            UploadService uploadService,
            DataRetentionAuditService auditService) {
        this.userRepository = userRepository;
        this.uploadService = uploadService;
        this.auditService = auditService;
    }
    
    /**
     * Scheduled job that runs daily at 2 AM to process data retention policies.
     * Identifies and processes users eligible for data deletion based on inactivity period.
     * Uses batch processing to handle large datasets efficiently.
     *
     * @throws DataRetentionException if the process fails critically
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(propagation = Propagation.REQUIRED)
    public void processDataRetention() {
        log.info("Starting scheduled data retention process");
        
        try {
            List<User> eligibleUsers = findEligibleUsers();
            log.info("Found {} users eligible for data retention processing", eligibleUsers.size());
            processUserDeletion(eligibleUsers);
            log.info("Completed data retention processing for {} users", eligibleUsers.size());
        } catch (Exception e) {
            log.error("Critical error during data retention process", e);
            throw new DataRetentionException("Failed to process data retention: " + e.getMessage(), e);
        }
    }
    
    /**
     * Identifies users eligible for data deletion based on inactivity period.
     * Users are considered eligible if they have been inactive for {@value RETENTION_YEARS} years
     * and haven't been previously marked for deletion.
     *
     * @return List of users eligible for data deletion
     */
    public List<User> findEligibleUsers() {
        Instant cutoffDate = Instant.now().minus(RETENTION_YEARS, ChronoUnit.YEARS);
        return userRepository.findByScheduledDeletionAtIsNullAndLastActivityAtBefore(cutoffDate);
    }
    
    /**
     * Processes data deletion for a list of users in batches.
     * Implements safe deletion with error handling and audit logging.
     *
     * @param users List of users to process for deletion
     * @throws IllegalArgumentException if users list is null
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void processUserDeletion(List<User> users) {
        Assert.notNull(users, "Users list cannot be null");
        
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            try {
                deleteUserData(user);
                auditService.logDeletion(user.getId(), DELETION_REASON, STATUS_SUCCESS);
                
                if (i % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            } catch (Exception e) {
                log.error("Failed to delete data for user {}: {}", user.getId(), e.getMessage());
                auditService.logDeletion(user.getId(), DELETION_REASON, STATUS_FAILED);
                // Continue processing other users but mark this batch as failed
                throw new DataRetentionException(
                    String.format("Failed to process user deletion for ID: %s at batch index: %d", user.getId(), i),
                    e
                );
            }
        }
    }
    
    /**
     * Deletes personal data for a specific user while maintaining referential integrity.
     * Removes uploaded files and anonymizes personal information in compliance with GDPR.
     *
     * @param user the user whose data should be deleted
     * @throws IllegalArgumentException if user is null
     * @throws DataRetentionException if critical data deletion fails
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected void deleteUserData(User user) {
        validateUserForDeletion(user);
        
        // Delete uploaded files
        safelyDeleteFile(user.getCvFilename());
        safelyDeleteFile(user.getDegreeFilename());
        safelyDeleteFile(user.getExaminationFilename());
        safelyDeleteFile(user.getAvatar());
        
        // Clear personal information
        user.setFirstName(DELETED_MARKER);
        user.setLastName(DELETED_MARKER);
        user.setEmail(null);
        user.setAvatar(null);
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setGender(null);
        user.setNationality(null);
        user.setMatriculationNumber(null);
        user.setCustomData(null);
        user.setScheduledDeletionAt(Instant.now());
        
        userRepository.save(user);
    }
    
    /**
     * Safely deletes a file if it exists, handling any errors that occur during deletion.
     * Logs errors but does not throw exceptions to allow the overall process to continue.
     *
     * @param filename the name of the file to delete
     */
    private void safelyDeleteFile(String filename) {
        if (filename != null && !filename.trim().isEmpty()) {
            try {
                uploadService.delete(filename);
                log.debug("Successfully deleted file: {}", filename);
            } catch (Exception e) {
                log.error("Failed to delete file {}: {}", filename, e.getMessage());
            }
        }
    }
    
    /**
     * Validates user data before deletion processing.
     * Ensures all required fields are present and valid.
     *
     * @param user the user to validate
     * @throws DataRetentionException if validation fails
     */
    private void validateUserForDeletion(User user) {
        if (user == null) {
            throw new DataRetentionException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new DataRetentionException("User ID cannot be null");
        }
        if (user.getScheduledDeletionAt() != null) {
            throw new DataRetentionException("User is already scheduled for deletion");
        }
    }
}