package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.UserGroup;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserGroupDto {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserGroupDto from(UserGroup group) {
        UserGroupDto dto = new UserGroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }
}
