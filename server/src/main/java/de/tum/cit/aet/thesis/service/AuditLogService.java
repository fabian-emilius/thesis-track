package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.GroupAuditLog;
import de.tum.cit.aet.thesis.repository.GroupAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for handling audit logging operations.
 * Centralizes audit logging logic to maintain consistency and avoid duplication.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final GroupAuditLogRepository auditLogRepository;
    private final AuthenticationService authenticationService;

    /**
     * Logs a group-related action in the audit log.
     *
     * @param groupId The ID of the group being acted upon
     * @param action The action being performed
     * @param details Additional details about the action
     */
    @Transactional
    public void logGroupAction(UUID groupId, String action, String details) {
        GroupAuditLog log = new GroupAuditLog();
        log.setGroupId(groupId);
        log.setUserId(authenticationService.getCurrentUser().getId());
        log.setAction(action);
        log.setDetails(details);
        
        auditLogRepository.save(log);
    }
}
