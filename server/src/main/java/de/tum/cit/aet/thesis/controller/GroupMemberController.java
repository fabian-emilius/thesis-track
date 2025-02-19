package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.controller.payload.AddGroupMemberPayload;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{slug}/members")
@RequiredArgsConstructor
@Tag(name = "Group Members", description = "APIs for managing research group members")
public class GroupMemberController {
    private final GroupService groupService;
    private final GroupMemberService memberService;
    private final GroupBasedAccessService accessService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all members of a research group", description = "Retrieves a list of all members belonging to the specified research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved group members"),
        @ApiResponse(responseCode = "403", description = "User does not have access to view group members"),
        @ApiResponse(responseCode = "404", description = "Research group not found")
    })
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(
            @Parameter(description = "Unique slug identifier of the research group") @PathVariable String slug) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.hasGroupAccess(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        return ResponseEntity.ok(memberService.getGroupMembers(group.getId()).stream()
                .map(GroupMemberDto::fromEntity)
                .toList());
    }

    @PostMapping
    @Operation(summary = "Add a new member to research group", description = "Adds a new member to the specified research group with defined role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Member successfully added to group"),
        @ApiResponse(responseCode = "403", description = "User does not have permission to manage group members"),
        @ApiResponse(responseCode = "404", description = "Research group or user not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<GroupMemberDto> addGroupMember(
            @Parameter(description = "Unique slug identifier of the research group") @PathVariable String slug,
            @Valid @RequestBody AddGroupMemberPayload payload
    ) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.canManageGroup(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        User user = userService.getUserById(payload.userId());
        return ResponseEntity.ok(GroupMemberDto.fromEntity(
                memberService.addMember(group, user, payload.role())
        ));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Remove a member from research group", description = "Removes the specified member from the research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Member successfully removed from group"),
        @ApiResponse(responseCode = "403", description = "User does not have permission to manage group members"),
        @ApiResponse(responseCode = "404", description = "Research group or member not found")
    })
    public ResponseEntity<Void> removeGroupMember(
            @Parameter(description = "Unique slug identifier of the research group") @PathVariable String slug,
            @Parameter(description = "UUID of the user to remove from group") @PathVariable UUID userId
    ) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.canManageGroup(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        memberService.removeMember(group.getId(), userId);
        return ResponseEntity.ok().build();
    }
}