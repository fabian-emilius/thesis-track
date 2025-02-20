package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.entity.GroupMember;
import lombok.Data;

import java.util.UUID;

/**
 * DTO for group member information.
 */
@Data
public class GroupMemberDto {
    private UUID userId;
    private String role;
    private String userName;
    private String userEmail;

    public static GroupMemberDto from(GroupMember member) {
        GroupMemberDto dto = new GroupMemberDto();
        dto.setUserId(member.getUser().getId());
        dto.setRole(member.getRole());
        dto.setUserName(member.getUser().getUsername());
        dto.setUserEmail(member.getUser().getEmail());
        return dto;
    }
}
