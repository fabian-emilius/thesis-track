package de.tum.cit.aet.thesis.repository;

import de.tum.cit.aet.thesis.entity.GroupAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupAuditLogRepository extends JpaRepository<GroupAuditLog, UUID> {
    List<GroupAuditLog> findByGroupIdOrderByTimestampDesc(UUID groupId);
    List<GroupAuditLog> findByUserIdOrderByTimestampDesc(UUID userId);
}
