package de.tum.cit.aet.thesis.entity;

import de.tum.cit.aet.thesis.constants.GroupRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "topics")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "topic_id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "thesis_types", columnDefinition = "text[]")
    private Set<String> thesisTypes = new HashSet<>();

    @NotNull
    @Column(name = "problem_statement", nullable = false)
    private String problemStatement;

    @NotNull
    @Column(name = "requirements", nullable = false)
    private String requirements;

    @NotNull
    @Column(name = "goals", nullable = false)
    private String goals;

    @NotNull
    @Column(name = "\"references\"", nullable = false)
    private String references;

    @Column(name = "closed_at")
    private Instant closedAt;

    @UpdateTimestamp
    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "topic", fetch = FetchType.EAGER)
    @OrderBy("position ASC")
    private List<TopicRole> roles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    public boolean hasReadAccess(User user) {
        // System admins always have access
        if (user.hasAnyGroup("admin")) {
            return true;
        }

        // Check if user has any role in the topic
        if (roles.stream().anyMatch(role -> role.getUser().getId().equals(user.getId()))) {
            return true;
        }

        // If topic belongs to a group, check group permissions
        if (group != null) {
            return user.getGroups().stream()
                    .anyMatch(ug -> ug.getGroup().getId().equals(group.getId()));
        }

        return false;
    }

    public boolean hasEditAccess(User user) {
        // System admins always have access
        if (user.hasAnyGroup("admin")) {
            return true;
        }

        // Check if user has supervisor role in the topic
        if (roles.stream().anyMatch(role -> role.getUser().getId().equals(user.getId()))) {
            return true;
        }

        // If topic belongs to a group, check if user is admin or supervisor
        if (group != null) {
            return user.getGroups().stream()
                    .filter(ug -> ug.getGroup().getId().equals(group.getId()))
                    .anyMatch(ug -> 
                        ug.getRole() == GroupRole.ADMIN || 
                        ug.getRole() == GroupRole.SUPERVISOR
                    );
        }

        return false;
    }
}