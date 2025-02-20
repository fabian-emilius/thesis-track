package de.tum.cit.aet.thesis.entity;

import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "theses")
@Getter
@Setter
public class Thesis {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThesisType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThesisState state;

    @Column(name = "final_grade")
    private Double finalGrade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "thesis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ThesisRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "thesis", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ThesisPresentation> presentations = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Set<User> getStudents() {
        Set<User> students = new HashSet<>();
        for (ThesisRole role : roles) {
            if (role.getRole().equals("STUDENT")) {
                students.add(role.getUser());
            }
        }
        return students;
    }

    public Set<User> getAdvisors() {
        Set<User> advisors = new HashSet<>();
        for (ThesisRole role : roles) {
            if (role.getRole().equals("ADVISOR")) {
                advisors.add(role.getUser());
            }
        }
        return advisors;
    }

    public boolean hasAdvisorAccess(User user) {
        return roles.stream()
                .anyMatch(role -> role.getUser().equals(user) && 
                        (role.getRole().equals("ADVISOR") || role.getRole().equals("SUPERVISOR")));
    }
}