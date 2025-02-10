package de.tum.cit.aet.thesis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Group entities.
 * Used for creating and updating groups.
 */
@Data
@Schema(description = "Group DTO for creating and updating groups")
public class GroupDto {
    @Schema(description = "Unique identifier of the group", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    
    @NotBlank(message = "Group name is required")
    @Size(min = 3, max = 255, message = "Group name must be between 3 and 255 characters")
    @Schema(description = "Name of the group", example = "Computer Science Department")
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Schema(description = "Description of the group", example = "Research group for computer science topics")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}