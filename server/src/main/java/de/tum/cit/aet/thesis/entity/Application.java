package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import de.tum.cit.aet.thesis.constants.ApplicationState;
import de.tum.cit.aet.thesis.constants.ApplicationRejectReason;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "applications")
@Getter
@Setter
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id", nullable = false)
    private java.util.UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "thesis_title")
    private String thesisTitle;

    @NotNull
    @Column(name = "thesis_type", nullable = false)
    private String thesisType;

    @NotNull
    @Column(name = "motivation", nullable = false)
    private String motivation;

    @NotNull
    @Column(name = "comment", nullable = false)
    private String comment;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private ApplicationState state;

    @Enumerated(EnumType.STRING)
    @Column(name = "reject_reason")
    private ApplicationRejectReason rejectReason;

    @NotNull
    @Column(name = "desired_start_date", nullable = false)
    private Instant desiredStartDate;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @CreationTimestamp
    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToMany(mappedBy = "application", fetch = FetchType.EAGER)
    private List<ApplicationReviewer> reviewers = new ArrayList<>();

    public Optional<ApplicationReviewer> getReviewer(User user) {
        for (ApplicationReviewer reviewer : reviewers) {
            if (reviewer.getUser().getId().equals(user.getId())) {
                return Optional.of(reviewer);
            }
        }

        return Optional.empty();
    }
}