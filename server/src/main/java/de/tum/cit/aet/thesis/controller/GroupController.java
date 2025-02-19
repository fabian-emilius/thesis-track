package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UploadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing groups.
 * Provides endpoints for group CRUD operations and settings management.
 */
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
public class GroupController {
    private final GroupService groupService;
    private final UploadService uploadService;

    /**
     * Retrieves all groups.
     *
     * @return List of all groups
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    /**
     * Retrieves a specific group by ID.
     *
     * @param groupId The UUID of the group
     * @return The requested group
     */
    @GetMapping(value = "/{groupId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> getGroup(@PathVariable @NotNull UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    /**
     * Creates a new group.
     * Requires admin role.
     *
     * @param groupDto The group data
     * @return The created group
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Group> createGroup(@RequestBody @Valid GroupDto groupDto) {
        Group group = mapDtoToEntity(groupDto);
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    /**
     * Updates an existing group.
     * Requires admin role or group admin permission.
     *
     * @param groupId The UUID of the group to update
     * @param groupDto The updated group data
     * @return The updated group
     */
    @PutMapping(value = "/{groupId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('admin') or @groupPermissionEvaluator.isGroupAdmin(#groupId)")
    public ResponseEntity<Group> updateGroup(
            @PathVariable @NotNull UUID groupId,
            @RequestBody @Valid GroupDto groupDto) {
        Group group = mapDtoToEntity(groupDto);
        return ResponseEntity.ok(groupService.updateGroup(groupId, group));
    }

    /**
     * Updates a group's settings.
     * Requires admin role or group admin permission.
     *
     * @param groupId The UUID of the group
     * @param settings The new settings JSON
     * @return No content on success
     */
    @PutMapping(value = "/{groupId}/settings", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('admin') or @groupPermissionEvaluator.isGroupAdmin(#groupId)")
    public ResponseEntity<Void> updateGroupSettings(
            @PathVariable @NotNull UUID groupId,
            @RequestBody String settings) {
        groupService.updateGroupSettings(groupId, settings);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads a new group logo.
     * Requires admin role or group admin permission.
     *
     * @param groupId The UUID of the group
     * @param file The logo file to upload
     * @return No content on success
     */
    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin') or @groupPermissionEvaluator.isGroupAdmin(#groupId)")
    public ResponseEntity<Void> uploadGroupLogo(
            @PathVariable @NotNull UUID groupId,
            @RequestParam("file") MultipartFile file) {
        String logoUrl = uploadService.uploadFile(file, "logos", "group-" + groupId);
        groupService.updateGroupLogo(groupId, logoUrl);
        return ResponseEntity.noContent().build();
    }

    private Group mapDtoToEntity(GroupDto dto) {
        Group group = new Group();
        group.setName(dto.getName());
        group.setSlug(dto.getSlug());
        group.setDescription(dto.getDescription());
        group.setWebsiteUrl(dto.getWebsiteUrl());
        group.setSettings(dto.getSettings());
        return group;
    }
}