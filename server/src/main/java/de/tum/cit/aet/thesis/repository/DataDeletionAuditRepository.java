package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.DataDeletionAudit;
import de.tum.cit.aet.thesis.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface DataDeletionAuditRepository extends JpaRepository<DataDeletionAudit, UUID> {
    
    /**
     * Find all audit records for a specific user
     * @param user The user to find audit records for
     * @return List of audit records
     */
    List<DataDeletionAudit> findByUser(User user);
    
    /**
     * Find audit records by deletion date range
     * @param start Start date
     * @param end End date
     * @param pageable Pagination information
     * @return Page of audit records
     */
    Page<DataDeletionAudit> findByDeletionDateBetween(Instant start, Instant end, Pageable pageable);
    
    /**
     * Find audit records by deletion type
     * @param deletionType The deletion type
     * @param pageable Pagination information
     * @return Page of audit records
     */
    Page<DataDeletionAudit> findByDeletionType(DataDeletionAudit.DeletionType deletionType, Pageable pageable);
    
    /**
     * Find audit records by status
     * @param status The deletion status
     * @param pageable Pagination information
     * @return Page of audit records
     */
    Page<DataDeletionAudit> findByStatus(DataDeletionAudit.DeletionStatus status, Pageable pageable);
}