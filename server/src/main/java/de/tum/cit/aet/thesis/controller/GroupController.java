package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.service.GroupService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing groups.
 * Provides endpoints for group CRUD operations and member management.
 */
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
public class GroupController {
    private final GroupService groupService;
    private final Bucket groupManagementBucket;

    /**
     * Retrieves all groups.
     *
     * @return List of all groups
     */
    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param groupId The ID of the group
     * @return The group details
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable @NotNull UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    /**
     * Retrieves a group by its slug.
     *
     * @param slug The slug of the group
     * @return The group details
     */
    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<GroupDto> getGroupBySlug(@PathVariable @NotBlank String slug) {
        return ResponseEntity.ok(groupService.getGroupBySlug(slug));
    }

    /**
     * Creates a new group.
     *
     * @param name The name of the group
     * @param slug The slug for the group
     * @param description Optional description
     * @param logoUrl Optional logo URL
     * @param externalLink Optional external link
     * @return The created group
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> createGroup(
            @RequestParam @NotBlank String name,
            @RequestParam @NotBlank String slug,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(required = false) String externalLink) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(groupService.createGroup(name, slug, description, logoUrl, externalLink));
    }

    /**
     * Updates an existing group.
     *
     * @param groupId The ID of the group to update
     * @param name The new name
     * @param description Optional new description
     * @param logoUrl Optional new logo URL
     * @param externalLink Optional new external link
     * @return The updated group
     */
    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateGroup(
            @PathVariable @NotNull UUID groupId,
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(required = false) String externalLink) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(groupService.updateGroup(groupId, name, description, logoUrl, externalLink));
    }

    /**
     * Updates group settings.
     *
     * @param groupId The ID of the group
     * @param settings The new settings
     * @return The updated settings
     */
    @PutMapping("/{groupId}/settings")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> updateGroupSettings(
            @PathVariable @NotNull UUID groupId,
            @RequestBody @Valid GroupSettingsDto settings) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(groupService.updateGroupSettings(groupId, settings));
    }

    /**
     * Deletes a group.
     *
     * @param groupId The ID of the group to delete
     * @return No content on success
     */
    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> deleteGroup(@PathVariable @NotNull UUID groupId) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all members of a group.
     *
     * @param groupId The ID of the group
     * @return List of group members
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable @NotNull UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    /**
     * Adds a member to a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user to add
     * @param role The role to assign
     * @return The created group membership
     */
    @PostMapping("/{groupId}/members")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> addGroupMember(
            @PathVariable @NotNull UUID groupId,
            @RequestParam @NotNull UUID userId,
            @RequestParam @NotBlank String role) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        return ResponseEntity.ok(groupService.addGroupMember(groupId, userId, role));
    }

    /**
     * Removes a member from a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user to remove
     * @return No content on success
     */
    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> removeGroupMember(
            @PathVariable @NotNull UUID groupId,
            @PathVariable @NotNull UUID userId) {
        if (!groupManagementBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        groupService.removeGroupMember(groupId, userId);
        return ResponseEntity.ok().build();
    }
}
