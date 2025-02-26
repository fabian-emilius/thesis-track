package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.repository.ApplicationRepository;
import de.tum.cit.aet.thesis.repository.ApplicationReviewerRepository;
import de.tum.cit.aet.thesis.repository.NotificationSettingRepository;
import de.tum.cit.aet.thesis.repository.ThesisCommentRepository;
import de.tum.cit.aet.thesis.repository.ThesisPresentationRepository;
import de.tum.cit.aet.thesis.repository.ThesisRoleRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing user data retention in compliance with GDPR requirements.
 * Handles identification and anonymization/deletion of user data that is older than the configured retention period.
 */
@Service
public class UserDataRetentionService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataRetentionService.class);
    
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationReviewerRepository applicationReviewerRepository;
    private final ThesisRoleRepository thesisRoleRepository;
    private final ThesisCommentRepository thesisCommentRepository;
    private final ThesisPresentationRepository thesisPresentationRepository;
    private final UploadService uploadService;
    
    @Value("${thesis-management.data-retention.user-data-years:10}")
    private int userDataRetentionYears;
    
    @Value("${thesis-management.data-retention.batch-size:100}")
    private int batchSize;

    public UserDataRetentionService(UserRepository userRepository,
                                   UserGroupRepository userGroupRepository,
                                   NotificationSettingRepository notificationSettingRepository,
                                   ApplicationRepository applicationRepository,
                                   ApplicationReviewerRepository applicationReviewerRepository,
                                   ThesisRoleRepository thesisRoleRepository,
                                   ThesisCommentRepository thesisCommentRepository,
                                   ThesisPresentationRepository thesisPresentationRepository,
                                   UploadService uploadService) {
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
        this.notificationSettingRepository = notificationSettingRepository;
        this.applicationRepository = applicationRepository;
        this.applicationReviewerRepository = applicationReviewerRepository;
        this.thesisRoleRepository = thesisRoleRepository;
        this.thesisCommentRepository = thesisCommentRepository;
        this.thesisPresentationRepository = thesisPresentationRepository;
        this.uploadService = uploadService;
    }

    /**
     * Identifies users eligible for GDPR data deletion (users inactive for more than the retention period).
     * 
     * @return List of user IDs eligible for deletion
     */
    public List<User> identifyUsersForDeletion() {
        // Calculate cutoff date (10 years or configured period)
        Instant cutoffDate = Instant.now().minus(userDataRetentionYears, ChronoUnit.YEARS);
        
        logger.info("Identifying users joined before {} for GDPR data deletion", cutoffDate);
        
        // Find users who joined before the cutoff date and haven't been updated since the cutoff date
        return userRepository.findByJoinedAtBeforeAndUpdatedAtBefore(cutoffDate, cutoffDate, batchSize);
    }
    
    /**
     * Processes a batch of users for GDPR data deletion.
     * 
     * @return The number of users processed
     */
    @Transactional
    public int processUserDataDeletion() {
        List<User> usersToProcess = identifyUsersForDeletion();
        
        if (usersToProcess.isEmpty()) {
            logger.info("No users found for GDPR data deletion");
            return 0;
        }
        
        logger.info("Processing {} users for GDPR data deletion", usersToProcess.size());
        
        for (User user : usersToProcess) {
            try {
                anonymizeUserData(user);
                logger.info("Successfully anonymized user data for user ID: {}", user.getId());
            } catch (Exception e) {
                logger.error("Error processing user ID: {} for GDPR deletion", user.getId(), e);
            }
        }
        
        return usersToProcess.size();
    }
    
    /**
     * Anonymizes or deletes user data in compliance with GDPR requirements.
     * 
     * @param user The user whose data should be anonymized/deleted
     */
    @Transactional
    public void anonymizeUserData(User user) {
        UUID userId = user.getId();
        logger.info("Anonymizing user data for user ID: {}", userId);
        
        // Delete user files (physical files)
        deleteUserFiles(user);
        
        // Anonymize personal data
        anonymizePersonalData(user);
        
        // Delete notification settings
        notificationSettingRepository.deleteByUserId(userId);
        
        // Save the anonymized user
        userRepository.save(user);
        
        logger.info("User data anonymization completed for user ID: {}", userId);
    }
    
    /**
     * Deletes all physical files associated with a user.
     * 
     * @param user The user whose files should be deleted
     */
    private void deleteUserFiles(User user) {
        // Delete CV file if exists
        if (user.getCvFilename() != null && !user.getCvFilename().isEmpty()) {
            try {
                uploadService.deleteFile(user.getCvFilename());
                logger.info("Deleted CV file: {} for user ID: {}", user.getCvFilename(), user.getId());
            } catch (Exception e) {
                logger.error("Error deleting CV file: {} for user ID: {}", user.getCvFilename(), user.getId(), e);
            }
        }
        
        // Delete degree file if exists
        if (user.getDegreeFilename() != null && !user.getDegreeFilename().isEmpty()) {
            try {
                uploadService.deleteFile(user.getDegreeFilename());
                logger.info("Deleted degree file: {} for user ID: {}", user.getDegreeFilename(), user.getId());
            } catch (Exception e) {
                logger.error("Error deleting degree file: {} for user ID: {}", user.getDegreeFilename(), user.getId(), e);
            }
        }
        
        // Delete examination file if exists
        if (user.getExaminationFilename() != null && !user.getExaminationFilename().isEmpty()) {
            try {
                uploadService.deleteFile(user.getExaminationFilename());
                logger.info("Deleted examination file: {} for user ID: {}", user.getExaminationFilename(), user.getId());
            } catch (Exception e) {
                logger.error("Error deleting examination file: {} for user ID: {}", user.getExaminationFilename(), user.getId(), e);
            }
        }
        
        // Delete avatar if exists and is not a Gravatar URL
        if (user.getAvatar() != null && !user.getAvatar().isEmpty() && !user.getAvatar().contains("gravatar.com")) {
            try {
                uploadService.deleteFile(user.getAvatar());
                logger.info("Deleted avatar file: {} for user ID: {}", user.getAvatar(), user.getId());
            } catch (Exception e) {
                logger.error("Error deleting avatar file: {} for user ID: {}", user.getAvatar(), user.getId(), e);
            }
        }
    }
    
    /**
     * Anonymizes personal user data.
     * 
     * @param user The user whose personal data should be anonymized
     */
    private void anonymizePersonalData(User user) {
        String anonymizedId = "anonymized-" + UUID.randomUUID();
        
        // Anonymize personal data (MUST DELETE per GDPR)
        user.setEmail(anonymizedId + "@anonymized.com");
        user.setFirstName("Anonymized");
        user.setLastName("User");
        user.setGender(null);
        user.setNationality(null);
        user.setMatriculationNumber(null);
        
        // Clear file references
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setAvatar(null);
        
        // Mark as anonymized in custom data
        user.getCustomData().put("anonymized", "true");
        user.getCustomData().put("anonymized_at", Instant.now().toString());
    }
}
