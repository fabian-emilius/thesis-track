package de.tum.cit.aet.thesis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Group entities.
 * Used for creating, updating, and retrieving group information.
 */
@Data
public class GroupDto {
    private UUID id;

    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 255, message = "Group name must be between 3 and 255 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
