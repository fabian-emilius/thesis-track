package de.tum.cit.aet.thesis.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateGroupPayload(
    @NotBlank
    String name,

    @NotBlank
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    String slug,

    String description,
    String websiteLink,
    String mailFooter,
    String acceptanceEmailText,
    String acceptanceInstructions
) {}