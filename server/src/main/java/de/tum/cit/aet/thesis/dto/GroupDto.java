package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.Group;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupDto {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int topicsCount;
    private int thesesCount;

    public static GroupDto from(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setActive(group.isActive());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        dto.setTopicsCount(group.getTopics().size());
        dto.setThesesCount(group.getTheses().size());
        return dto;
    }
}