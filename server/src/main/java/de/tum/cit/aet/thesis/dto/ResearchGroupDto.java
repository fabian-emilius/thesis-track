package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.ResearchGroup;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ResearchGroupDto(
    UUID id,
    String slug,
    String name,
    String description,
    String logoFilename,
    String websiteLink,
    String mailFooter,
    String acceptanceEmailText,
    String acceptanceInstructions,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static ResearchGroupDto fromEntity(ResearchGroup group) {
        return new ResearchGroupDto(
            group.getId(),
            group.getSlug(),
            group.getName(),
            group.getDescription(),
            group.getLogoFilename(),
            group.getWebsiteLink(),
            group.getMailFooter(),
            group.getAcceptanceEmailText(),
            group.getAcceptanceInstructions(),
            group.getCreatedAt(),
            group.getUpdatedAt()
        );
    }
}