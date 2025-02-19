package de.tum.cit.aet.thesis.controller.payload;

import jakarta.validation.constraints.NotBlank;

public record UpdateGroupPayload(
    @NotBlank
    String name,
    String description,
    String websiteLink,
    String mailFooter,
    String acceptanceEmailText,
    String acceptanceInstructions
) {}