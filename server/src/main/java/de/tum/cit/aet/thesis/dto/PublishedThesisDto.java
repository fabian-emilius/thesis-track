package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class PublishedThesisDto {
    private UUID id;
    private ThesisDto thesis;
    private Set<UUID> visibilityGroups;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PublishedThesisDto from(PublishedThesis publishedThesis) {
        PublishedThesisDto dto = new PublishedThesisDto();
        dto.setId(publishedThesis.getId());
        dto.setThesis(ThesisDto.from(publishedThesis.getThesis()));
        dto.setVisibilityGroups(publishedThesis.getVisibilityGroups());
        dto.setCreatedAt(publishedThesis.getCreatedAt());
        dto.setUpdatedAt(publishedThesis.getUpdatedAt());
        return dto;
    }
}
