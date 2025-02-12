package de.tum.cit.aet.thesis.entity;

import de.tum.cit.aet.thesis.entity.key.GroupMemberId;
import de.tum.cit.aet.thesis.constants.GroupRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "group_members")
@IdClass(GroupMemberId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {
    @Id
    @Column(name = "group_id")
    private UUID groupId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
