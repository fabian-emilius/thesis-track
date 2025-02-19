package de.tum.cit.aet.thesis.controller.payload;

import de.tum.cit.aet.thesis.constants.GroupRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddGroupMemberPayload(
    @NotNull
    UUID userId,

    @NotNull
    GroupRole role
) {}