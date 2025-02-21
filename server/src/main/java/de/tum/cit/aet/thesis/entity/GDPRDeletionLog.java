package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gdpr_deletion_log")
@Getter
@Setter
public class GDPRDeletionLog {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
}
