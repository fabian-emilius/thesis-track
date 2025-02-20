package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import de.tum.cit.aet.thesis.constants.ApplicationRejectReason;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.constants.GroupRole;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id", nullable = false)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "thesis_title")
    private String thesisTitle;

    @Column(name = "thesis_type")
    private String thesisType;

    @NotNull
    @Column(name = "motivation", nullable = false)
    private String motivation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ApplicationState state;

    @NotNull
    @Column(name = "desired_start_date", nullable = false)
    private Instant desiredStartDate;

    @NotNull
    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason")
    private ApplicationRejectReason rejectReason;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    @OrderBy("reviewedAt ASC")
    private List<ApplicationReviewer> reviewers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    private boolean hasGroupRole(User user, GroupRole... allowedRoles) {
        if (group == null) return false;
        
        UserGroup userGroup = user.getGroupMembership(group);
        if (userGroup == null) return false;

        for (GroupRole role : allowedRoles) {
            if (userGroup.getRole() == role) return true;
        }
        return false;
    }

    public boolean hasReadAccess(User user) {
        if (user.hasAnyGroup("admin", "advisor", "supervisor")) {
            return true;
        }

        if (hasGroupRole(user, GroupRole.ADMIN, GroupRole.SUPERVISOR, GroupRole.ADVISOR)) {
            return true;
        }

        return this.user.getId().equals(user.getId());
    }

    public boolean hasEditAccess(User user) {
        if (user.hasAnyGroup("admin")) {
            return true;
        }

        if (hasGroupRole(user, GroupRole.ADMIN)) {
            return true;
        }

        return this.user.getId().equals(user.getId());
    }

    public boolean hasManagementAccess(User user) {
        if (user.hasAnyGroup("admin", "advisor", "supervisor")) {
            return true;
        }

        return hasGroupRole(user, GroupRole.ADMIN, GroupRole.SUPERVISOR, GroupRole.ADVISOR);
    }

    public Optional<ApplicationReviewer> getReviewer(User user) {
        for (ApplicationReviewer reviewer : getReviewers()) {
            if (reviewer.getUser().getId().equals(user.getId())) {
                return Optional.of(reviewer);
            }
        }

        return Optional.empty();
    }
}