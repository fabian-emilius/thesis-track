package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.*;
import de.tum.cit.aet.thesis.repository.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Service for managing user data retention and deletion in compliance with GDPR requirements.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataRetentionService {

    private final UserRepository userRepository;
    private final ThesisRepository thesisRepository;
    private final ThesisRoleRepository thesisRoleRepository;
    private final ThesisCommentRepository thesisCommentRepository;
    private final ThesisFileRepository thesisFileRepository;
    private final ThesisPresentationRepository thesisPresentationRepository;
    private final ThesisAssessmentRepository thesisAssessmentRepository;
    private final NotificationSettingRepository notificationSettingRepository;
    private final UserGroupRepository userGroupRepository;
    private final DataDeletionAuditRepository dataDeletionAuditRepository;
    private final UploadService uploadService;

    @Getter
    @Value("${thesis-management.retention.period.years:10}")
    private int retentionPeriodYears;

    @Getter
    @Value("${thesis-management.retention.batch.size:10}")
    private int batchSize;

    /**
     * Identifies users whose data should be deleted based on retention period.
     *
     * @return List of users eligible for deletion
     */
    public List<User> identifyUsersForDeletion() {
        // Calculate the threshold date (current time minus retention period)
        Instant thresholdDate = Instant.now().minus(Duration.ofDays(365L * retentionPeriodYears));
        
        // Find users whose retention period has expired
        return userRepository.findByRetentionStartDateBefore(thresholdDate, PageRequest.of(0, batchSize));
    }

    /**
     * Process data deletion for users whose retention period has expired.
     * This runs as part of a scheduled job.
     *
     * @return Number of users processed
     */
    @Transactional
    public int processDataRetention() {
        List<User> usersForDeletion = identifyUsersForDeletion();
        log.info("Found {} users eligible for data deletion", usersForDeletion.size());

        int processedCount = 0;
        for (User user : usersForDeletion) {
            try {
                deleteUserData(user, DataDeletionAudit.DeletionType.AUTOMATIC_RETENTION);
                processedCount++;
            } catch (Exception e) {
                log.error("Error deleting user data for user ID: {}", user.getId(), e);
                // Create audit entry for failed deletion
                createDeletionAudit(
                    user,
                    DataDeletionAudit.DeletionType.AUTOMATIC_RETENTION,
                    Collections.singletonMap("error", e.getMessage()),
                    DataDeletionAudit.DeletionStatus.FAILURE
                );
            }
        }

        return processedCount;
    }

    /**
     * Delete a user's data manually (triggered by admin).
     *
     * @param userId The ID of the user to delete
     * @return true if deletion was successful
     */
    @Transactional
    public boolean deleteUserDataManually(UUID userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        try {
            deleteUserData(userOpt.get(), DataDeletionAudit.DeletionType.ADMIN_TRIGGERED);
            return true;
        } catch (Exception e) {
            log.error("Error manually deleting user data for user ID: {}", userId, e);
            // Create audit entry for failed deletion
            createDeletionAudit(
                userOpt.get(),
                DataDeletionAudit.DeletionType.ADMIN_TRIGGERED,
                Collections.singletonMap("error", e.getMessage()),
                DataDeletionAudit.DeletionStatus.FAILURE
            );
            return false;
        }
    }

    /**
     * Core method to delete all user data and create audit log.
     *
     * @param user The user whose data should be deleted
     * @param deletionType The type of deletion operation
     */
    @Transactional
    public void deleteUserData(User user, DataDeletionAudit.DeletionType deletionType) {
        log.info("Deleting data for user ID: {} ({})", user.getId(), deletionType);
        Map<String, Object> affectedRecords = new HashMap<>();
        boolean partialFailure = false;

        try {
            // 1. Delete notification settings
            int notificationCount = deleteNotificationSettings(user);
            affectedRecords.put("notificationSettings", notificationCount);

            // 2. Delete user groups
            int userGroupCount = deleteUserGroups(user);
            affectedRecords.put("userGroups", userGroupCount);

            // 3. Delete thesis comments
            int commentsCount = deleteThesisComments(user);
            affectedRecords.put("thesisComments", commentsCount);

            // 4. Handle thesis files (requires special handling for actual file storage)
            List<String> filePathsToDelete = getFilePathsForUser(user);
            affectedRecords.put("thesisFiles", filePathsToDelete.size());

            // Delete physical files
            for (String filePath : filePathsToDelete) {
                try {
                    uploadService.deleteFile(filePath);
                } catch (Exception e) {
                    log.error("Error deleting file: {}", filePath, e);
                    partialFailure = true;
                }
            }

            // 5. Handle thesis assessments
            int assessmentsCount = deleteThesisAssessments(user);
            affectedRecords.put("thesisAssessments", assessmentsCount);

            // 6. Delete thesis presentations
            int presentationsCount = deleteThesisPresentations(user);
            affectedRecords.put("thesisPresentations", presentationsCount);

            // 7. Delete thesis roles
            int rolesCount = deleteThesisRoles(user);
            affectedRecords.put("thesisRoles", rolesCount);

            // 8. Delete theses where user is student
            int thesesCount = markThesesAsAnonymized(user);
            affectedRecords.put("theses", thesesCount);

            // 9. Finally, anonymize the user record itself
            anonymizeUser(user);

            // Create an audit log entry
            DataDeletionAudit.DeletionStatus status = partialFailure ? 
                DataDeletionAudit.DeletionStatus.PARTIAL_SUCCESS : 
                DataDeletionAudit.DeletionStatus.SUCCESS;
                
            createDeletionAudit(user, deletionType, affectedRecords, status);

        } catch (Exception e) {
            log.error("Error during user data deletion process for user ID: {}", user.getId(), e);
            createDeletionAudit(
                user,
                deletionType,
                affectedRecords,
                DataDeletionAudit.DeletionStatus.FAILURE
            );
            throw e; // Re-throw to rollback transaction
        }
    }

    /**
     * Delete notification settings for a user
     */
    private int deleteNotificationSettings(User user) {
        List<NotificationSetting> settings = notificationSettingRepository.findByUser(user);
        int count = settings.size();
        notificationSettingRepository.deleteAll(settings);
        return count;
    }

    /**
     * Delete user group associations
     */
    private int deleteUserGroups(User user) {
        List<UserGroup> userGroups = userGroupRepository.findByUser(user);
        int count = userGroups.size();
        userGroupRepository.deleteAll(userGroups);
        return count;
    }

    /**
     * Delete thesis comments created by user
     */
    private int deleteThesisComments(User user) {
        List<ThesisComment> comments = thesisCommentRepository.findByCreatedBy(user);
        int count = comments.size();
        thesisCommentRepository.deleteAll(comments);
        return count;
    }

    /**
     * Get paths of files to be deleted
     */
    private List<String> getFilePathsForUser(User user) {
        List<String> filePaths = new ArrayList<>();
        
        // Add user-specific files
        if (user.getCvFilename() != null) filePaths.add(user.getCvFilename());
        if (user.getDegreeFilename() != null) filePaths.add(user.getDegreeFilename());
        if (user.getExaminationFilename() != null) filePaths.add(user.getExaminationFilename());
        
        // Add thesis files uploaded by this user
        List<ThesisFile> thesisFiles = thesisFileRepository.findByUploadedBy(user);
        for (ThesisFile file : thesisFiles) {
            filePaths.add(file.getPath());
        }
        
        thesisFileRepository.deleteAll(thesisFiles);
        return filePaths;
    }

    /**
     * Delete thesis assessments created by user
     */
    private int deleteThesisAssessments(User user) {
        List<ThesisAssessment> assessments = thesisAssessmentRepository.findByCreatedBy(user);
        int count = assessments.size();
        thesisAssessmentRepository.deleteAll(assessments);
        return count;
    }

    /**
     * Delete thesis presentations created by user
     */
    private int deleteThesisPresentations(User user) {
        List<ThesisPresentation> presentations = thesisPresentationRepository.findByCreatedBy(user);
        int count = presentations.size();
        thesisPresentationRepository.deleteAll(presentations);
        return count;
    }

    /**
     * Delete thesis roles associated with user
     */
    private int deleteThesisRoles(User user) {
        List<ThesisRole> roles = thesisRoleRepository.findByUser(user);
        int count = roles.size();
        thesisRoleRepository.deleteAll(roles);
        return count;
    }

    /**
     * Mark theses as anonymized instead of deleting them
     * This preserves academic records while removing personal information
     */
    private int markThesesAsAnonymized(User user) {
        // Find theses where user is a student
        List<Thesis> theses = thesisRepository.findThesesByStudentId(user.getId());
        int count = theses.size();
        
        // We don't delete theses but mark them as anonymized
        for (Thesis thesis : theses) {
            // Set student-related fields to null or "ANONYMOUS"
            thesis.getMetadata().setAnonymized(true);
            thesis.getMetadata().setStudentMatriculationNumber("ANONYMIZED");
            thesisRepository.save(thesis);
        }
        
        return count;
    }

    /**
     * Anonymize a user record rather than deleting it
     * This preserves the record but removes all personal data
     */
    private void anonymizeUser(User user) {
        // Generate an anonymized ID that maintains uniqueness
        String anonymizedId = "ANONYMIZED-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Clear all personal information
        user.setEmail(null);
        user.setAvatar(null);
        user.setFirstName(null);
        user.setLastName(null);
        user.setGender(null);
        user.setNationality(null);
        user.setCvFilename(null);
        user.setDegreeFilename(null);
        user.setExaminationFilename(null);
        user.setStudyDegree(null);
        user.setStudyProgram(null);
        user.setProjects(null);
        user.setInterests(null);
        user.setSpecialSkills(null);
        user.setCustomData(new HashMap<>());
        user.setMatriculationNumber(anonymizedId);
        
        // Save the anonymized user
        userRepository.save(user);
    }

    /**
     * Create an audit record for a deletion operation
     */
    private void createDeletionAudit(User user, DataDeletionAudit.DeletionType deletionType, 
                                    Map<String, Object> affectedRecords, DataDeletionAudit.DeletionStatus status) {
        DataDeletionAudit audit = new DataDeletionAudit();
        audit.setDeletionDate(Instant.now());
        audit.setUser(user);
        audit.setDeletionType(deletionType);
        audit.setAffectedRecords(affectedRecords);
        audit.setStatus(status);
        
        dataDeletionAuditRepository.save(audit);
    }

    /**
     * Get deletion audit records with pagination
     *
     * @param page Page number
     * @param size Page size
     * @return Page of audit records
     */
    public Page<DataDeletionAudit> getAuditRecords(int page, int size) {
        return dataDeletionAuditRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Get deletion audit records by type with pagination
     *
     * @param type Deletion type
     * @param page Page number
     * @param size Page size
     * @return Page of audit records
     */
    public Page<DataDeletionAudit> getAuditRecordsByType(DataDeletionAudit.DeletionType type, int page, int size) {
        return dataDeletionAuditRepository.findByDeletionType(type, PageRequest.of(page, size));
    }

    /**
     * Get deletion audit records by status with pagination
     *
     * @param status Deletion status
     * @param page Page number
     * @param size Page size
     * @return Page of audit records
     */
    public Page<DataDeletionAudit> getAuditRecordsByStatus(DataDeletionAudit.DeletionStatus status, int page, int size) {
        return dataDeletionAuditRepository.findByStatus(status, PageRequest.of(page, size));
    }
}