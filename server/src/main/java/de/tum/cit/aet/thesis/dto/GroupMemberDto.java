package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.GroupMember;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupMemberDto {
    private UUID userId;
    private String role;
    private LightUserDto user;

    public static GroupMemberDto from(GroupMember member) {
        GroupMemberDto dto = new GroupMemberDto();
        dto.setUserId(member.getUser().getId());
        dto.setRole(member.getRole());
        dto.setUser(LightUserDto.from(member.getUser()));
        return dto;
    }
}
