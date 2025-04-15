package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Entity for tracking data deletion operations for GDPR compliance.
 */
@Getter
@Setter
@Entity
@Table(name = "data_deletion_audit")
public class DataDeletionAudit {
    
    public enum DeletionType {
        AUTOMATIC_RETENTION, // Deleted automatically after retention period
        ADMIN_TRIGGERED,     // Admin manually triggered deletion
        USER_REQUESTED       // User requested "right to be forgotten"
    }
    
    public enum DeletionStatus {
        SUCCESS,
        PARTIAL_SUCCESS,
        FAILURE
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @NotNull
    @Column(name = "deletion_date", nullable = false)
    private Instant deletionDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "deletion_type", nullable = false, length = 50)
    private DeletionType deletionType;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "affected_records", columnDefinition = "jsonb")
    private Map<String, Object> affectedRecords;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeletionStatus status;
    
    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}