package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entity representing a group (e.g., department, chair) in the system.
 * Groups provide isolation and customization for different organizational units.
 */
@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {
    @Id
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Size(min = 2, max = 50, message = "Slug must be between 2 and 50 characters")
    @Column(nullable = false, unique = true)
    private String slug;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Pattern(regexp = "^(https?://)?[^\s/$.?#].[^\s]*$", message = "Invalid website URL format")
    @Column(name = "website_url")
    private String websiteUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> settings;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
}