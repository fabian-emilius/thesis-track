package de.tum.cit.aet.thesis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for group information")
public class GroupDto {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    @Schema(description = "Name of the group", example = "Software Engineering Group")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 50, message = "Slug must not exceed 50 characters")
    @Pattern(regexp = "^[a-z0-9][a-z0-9-]*[a-z0-9]$", 
            message = "Slug must contain only lowercase letters, numbers, and hyphens, must start and end with alphanumeric character")
    @Schema(description = "URL-friendly identifier for the group", example = "software-engineering")
    private String slug;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Detailed description of the group", example = "Research group focusing on software engineering principles")
    private String description;

    @Size(max = 255, message = "Link must not exceed 255 characters")
    @Pattern(regexp = "^(https?://.*)?$", message = "Link must be a valid URL starting with http:// or https://")
    @Schema(description = "Website URL of the group", example = "https://example.com/group")
    private String link;

    @Size(max = 1000, message = "Mail footer must not exceed 1000 characters")
    @Schema(description = "Custom footer text for group emails", example = "Best regards,\nSoftware Engineering Group")
    private String mailFooter;

    @Size(max = 4000, message = "Acceptance text must not exceed 4000 characters")
    @Schema(description = "Text shown when accepting group membership", example = "By accepting, you agree to follow group guidelines")
    private String acceptanceText;
}
