package de.tum.cit.aet.thesis.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Group entities.
 * Used for API requests and responses, providing a clean interface for group data.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Size(min = 2, max = 50, message = "Slug must be between 2 and 50 characters")
    private String slug;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private String logoUrl;

    @Pattern(regexp = "^https?://.*$", message = "Website URL must be a valid HTTP(S) URL")
    private String websiteUrl;

    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}