package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.GDPRDeletionLog;
import de.tum.cit.aet.thesis.repository.UserRepository;
import de.tum.cit.aet.thesis.repository.GDPRDeletionLogRepository;
import de.tum.cit.aet.thesis.service.MailingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * Service responsible for managing GDPR-compliant user data deletion.
 * Handles the identification, notification, and deletion of inactive user data
 * according to GDPR requirements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GDPRCleanupService {

    private final UserRepository userRepository;
    private final GDPRDeletionLogRepository gdprDeletionLogRepository;
    private final MailingService mailingService;
    private final UploadService uploadService;

    @Value("${gdpr.retention.years:10}")
    private int retentionYears;

    @Value("${gdpr.notification.days:30}")
    private int notificationDays;

    @Value("${upload.base-path}")
    private String uploadBasePath;

    /**
     * Identifies users who have been inactive for longer than the retention period
     * and have not yet been scheduled for deletion.
     *
     * @return List of users eligible for GDPR deletion
     */
    public List<User> findUsersEligibleForDeletion() {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(retentionYears, ChronoUnit.YEARS);
        log.info("Searching for users inactive since {}", cutoffDate);
        
        List<User> users = userRepository.findByLastActivityDateBeforeAndScheduledDeletionDateIsNull(cutoffDate);
        log.info("Found {} users eligible for GDPR deletion", users.size());
        
        return users;
    }

    /**
     * Schedules the deletion of user data and sends notification emails.
     * Users will be notified of the upcoming deletion and given time to prevent it
     * by logging in.
     *
     * @param users List of users to schedule for deletion
     */
    @Transactional
    public void scheduleUsersForDeletion(List<User> users) {
        if (users.isEmpty()) {
            log.debug("No users to schedule for deletion");
            return;
        }

        LocalDateTime scheduledDeletionDate = LocalDateTime.now().plus(notificationDays, ChronoUnit.DAYS);
        log.info("Scheduling {} users for deletion on {}", users.size(), scheduledDeletionDate);
        
        users.forEach(user -> scheduleUserForDeletion(user, scheduledDeletionDate));
    }

    /**
     * Schedules an individual user for deletion and sends notification.
     */
    private void scheduleUserForDeletion(User user, LocalDateTime scheduledDeletionDate) {
        try {
            user.setScheduledDeletionDate(scheduledDeletionDate);
            user.setDeletionNotifiedAt(LocalDateTime.now());
            userRepository.save(user);

            mailingService.sendGDPRDeletionNotification(user, scheduledDeletionDate);
            log.debug("Scheduled deletion for user {}", user.getId());
        } catch (Exception e) {
            log.error("Error scheduling deletion for user {}", user.getId(), e);
        }
    }

    /**
     * Executes the deletion process for all users whose scheduled deletion date
     * has passed. This includes data anonymization and file deletion.
     */
    @Transactional
    public void executeScheduledDeletions() {
        List<User> usersToDelete = userRepository.findByScheduledDeletionDateBefore(LocalDateTime.now());
        if (usersToDelete.isEmpty()) {
            log.debug("No users scheduled for deletion");
            return;
        }

        log.info("Executing scheduled deletions for {} users", usersToDelete.size());
        usersToDelete.forEach(this::deleteUserData);
    }

    /**
     * Handles the complete deletion/anonymization of a user's data.
     * This includes:
     * - Anonymizing personal information
     * - Deleting associated files
     * - Creating an audit log entry
     * 
     * @param user The user whose data should be deleted
     */
    @Transactional
    private void deleteUserData(User user) {
        log.debug("Starting data deletion for user {}", user.getId());
        GDPRDeletionLog log = createDeletionLog(user);

        try {
            anonymizeUserData(user);
            deleteUserFiles(user);
            
            userRepository.save(user);
            gdprDeletionLogRepository.save(log);
            
            log.info("Successfully completed data deletion for user {}", user.getId());
        } catch (Exception e) {
            log.error("Error during data deletion for user {}", user.getId(), e);
            throw e; // Rethrow to trigger transaction rollback
        }
    }

    /**
     * Creates a deletion log entry for audit purposes.
     */
    private GDPRDeletionLog createDeletionLog(User user) {
        GDPRDeletionLog log = new GDPRDeletionLog();
        log.setId(UUID.randomUUID());
        log.setUserId(user.getId());
        log.setDeletedAt(LocalDateTime.now());
        log.setReason(String.format("Automated GDPR cleanup after %d years of inactivity", retentionYears));
        return log;
    }

    /**
     * Anonymizes a user's personal data while preserving system references.
     */
    private void anonymizeUserData(User user) {
        user.setFirstName("[DELETED]");
        user.setLastName("[DELETED]");
        user.setEmail(String.format("deleted-%s@deleted.local", user.getId()));
        user.setMatriculationNumber(null);
        user.setDeletedAt(LocalDateTime.now());
    }

    /**
     * Deletes all files associated with a user from the file system.
     * 
     * @param user The user whose files should be deleted
     * @throws RuntimeException if file deletion fails
     */
    private void deleteUserFiles(User user) {
        Path userFilesPath = Paths.get(uploadBasePath, "user_files", user.getId().toString());
        log.debug("Deleting user files at {}", userFilesPath);

        try {
            if (Files.exists(userFilesPath)) {
                uploadService.deleteDirectory(userFilesPath);
                log.info("Successfully deleted files for user {}", user.getId());
            }
        } catch (Exception e) {
            log.error("Error deleting files for user {}", user.getId(), e);
            throw new RuntimeException("Failed to delete user files", e);
        }
    }
}
