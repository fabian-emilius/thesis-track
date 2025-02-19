package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "research_groups")
@Getter
@Setter
public class ResearchGroup {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String logoFilename;

    private String websiteLink;

    @Column(columnDefinition = "TEXT")
    private String mailFooter;

    @Column(columnDefinition = "TEXT")
    private String acceptanceEmailText;

    @Column(columnDefinition = "TEXT")
    private String acceptanceInstructions;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "group")
    private Set<GroupMember> members;

    @OneToMany(mappedBy = "group")
    private Set<Topic> topics;

    @OneToMany(mappedBy = "group")
    private Set<Application> applications;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}