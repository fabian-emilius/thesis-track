package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.Group;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class GroupDto {
    private UUID id;
    private String slug;
    private String name;
    private String description;
    private String logoUrl;
    private String externalLink;
    private Instant createdAt;
    private Instant updatedAt;

    public static GroupDto from(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setSlug(group.getSlug());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setLogoUrl(group.getLogoUrl());
        dto.setExternalLink(group.getExternalLink());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }
}
