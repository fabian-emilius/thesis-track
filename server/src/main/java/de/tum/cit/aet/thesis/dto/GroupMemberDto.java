package de.tum.cit.aet.thesis.dto;

import de.tum.cit.aet.thesis.constants.GroupRole;
import lombok.Data;

import java.util.UUID;

@Data
public class GroupMemberDto {
    private UUID userId;
    private String username;
    private String fullName;
    private GroupRole role;
}