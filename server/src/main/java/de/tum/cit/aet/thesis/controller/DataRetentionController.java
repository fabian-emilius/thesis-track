package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.DataDeletionAudit;
import de.tum.cit.aet.thesis.service.UserDataRetentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Controller for data retention management (admin only).
 * Provides endpoints for checking data retention status, retrieving audit logs,
 * and manually triggering user data deletion.
 */
@RestController
@RequestMapping("/admin/data-retention")
@PreAuthorize("hasRole('admin')")
@RequiredArgsConstructor
public class DataRetentionController {

    private final UserDataRetentionService userDataRetentionService;

    /**
     * Get data retention audit logs with pagination
     *
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Paged list of audit records
     */
    @GetMapping("/audit")
    public ResponseEntity<Page<DataDeletionAudit>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userDataRetentionService.getAuditRecords(page, size));
    }

    /**
     * Get audit logs filtered by deletion type
     *
     * @param type Deletion type (AUTOMATIC_RETENTION, ADMIN_TRIGGERED, USER_REQUESTED)
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Filtered paged list of audit records
     */
    @GetMapping("/audit/type/{type}")
    public ResponseEntity<Page<DataDeletionAudit>> getAuditLogsByType(
            @PathVariable DataDeletionAudit.DeletionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userDataRetentionService.getAuditRecordsByType(type, page, size));
    }

    /**
     * Get audit logs filtered by status
     *
     * @param status Status (SUCCESS, PARTIAL_SUCCESS, FAILURE)
     * @param page Page number (0-based)
     * @param size Number of records per page
     * @return Filtered paged list of audit records
     */
    @GetMapping("/audit/status/{status}")
    public ResponseEntity<Page<DataDeletionAudit>> getAuditLogsByStatus(
            @PathVariable DataDeletionAudit.DeletionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userDataRetentionService.getAuditRecordsByStatus(status, page, size));
    }

    /**
     * Manually trigger data deletion for a specific user
     *
     * @param userId ID of the user to delete
     * @return Success/failure response
     */
    @PostMapping("/delete/{userId}")
    public ResponseEntity<Map<String, Object>> triggerUserDeletion(@PathVariable UUID userId) {
        boolean success = userDataRetentionService.deleteUserDataManually(userId);
        
        if (success) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User data deletion triggered successfully"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to delete user data"
            ));
        }
    }

    /**
     * Get the current retention period configuration
     *
     * @return Retention period info
     */
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getRetentionConfig() {
        return ResponseEntity.ok(Map.of(
            "retentionPeriodYears", userDataRetentionService.getRetentionPeriodYears(),
            "batchSize", userDataRetentionService.getBatchSize()
        ));
    }
}