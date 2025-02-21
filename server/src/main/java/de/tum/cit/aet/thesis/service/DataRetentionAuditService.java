package de.tum.cit.aet.thesis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;

/**
 * Service responsible for auditing data retention operations.
 * Provides a centralized way to log data retention activities for compliance and monitoring.
 */
@Slf4j
@Service
public class DataRetentionAuditService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Logs a data retention operation.
     * This method runs in a new transaction to ensure audit logs are saved even if the parent transaction fails.
     *
     * @param userId The ID of the user being processed
     * @param reason The reason for the data retention operation
     * @param status The status of the operation
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logOperation(UUID userId, String reason, String status) {
        try {
            entityManager.createNativeQuery(
                "INSERT INTO data_retention_audit_log (user_id, deletion_timestamp, deletion_reason, deletion_status) " +
                "VALUES (?1, CURRENT_TIMESTAMP, ?2, ?3)"
            )
            .setParameter(1, userId)
            .setParameter(2, reason)
            .setParameter(3, status)
            .executeUpdate();
        } catch (Exception e) {
            log.error("Failed to create audit log for user " + userId, e);
            // We don't throw here to prevent audit logging from affecting the main operation
        }
    }
}