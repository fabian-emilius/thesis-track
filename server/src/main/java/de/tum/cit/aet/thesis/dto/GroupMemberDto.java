package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GroupMemberDto(
    UUID userId,
    String firstName,
    String lastName,
    String email,
    GroupRole role,
    OffsetDateTime joinedAt
) {
    public static GroupMemberDto fromEntity(GroupMember member) {
        return new GroupMemberDto(
            member.getUser().getId(),
            member.getUser().getFirstName(),
            member.getUser().getLastName(),
            member.getUser().getEmail(),
            member.getRole(),
            member.getJoinedAt()
        );
    }
}