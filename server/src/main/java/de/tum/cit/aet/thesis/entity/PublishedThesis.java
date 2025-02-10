package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "published_theses")
@Getter
@Setter
public class PublishedThesis {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "thesis_id", nullable = false)
    private Thesis thesis;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String abstract;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "visibility_groups", columnDefinition = "uuid[]")
    private Set<UUID> visibilityGroups = new HashSet<>();

    @Column(name = "published_at", nullable = false)
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        publishedAt = LocalDateTime.now();
    }
}