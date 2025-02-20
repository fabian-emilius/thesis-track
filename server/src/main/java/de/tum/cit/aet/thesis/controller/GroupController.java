package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for managing groups.
 * Provides endpoints for group CRUD operations and settings management.
 */
@Slf4j
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UploadService uploadService;

    /**
     * Retrieves all available groups.
     * @return List of all groups
     */
    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    /**
     * Retrieves a specific group by ID.
     * @param groupId ID of the group to retrieve
     * @return The requested group
     */
    @GetMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'MEMBER')")
    public ResponseEntity<Group> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    /**
     * Creates a new group.
     * @param group Group data to create
     * @return The created group
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    /**
     * Updates an existing group.
     * @param groupId ID of the group to update
     * @param group Updated group data
     * @return The updated group
     */
    @PutMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody Group group
    ) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, group));
    }

    /**
     * Updates a group's settings.
     * @param groupId ID of the group to update
     * @param settings New settings to apply
     * @return The updated group
     */
    @PutMapping("/{groupId}/settings")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    public ResponseEntity<Group> updateGroupSettings(
            @PathVariable UUID groupId,
            @RequestBody Map<String, Object> settings
    ) {
        return ResponseEntity.ok(groupService.updateGroupSettings(groupId, settings));
    }

    /**
     * Uploads a new logo for a group.
     * @param groupId ID of the group
     * @param file Logo file to upload
     * @return The updated group
     */
    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    public ResponseEntity<Group> uploadGroupLogo(
            @PathVariable UUID groupId,
            @RequestParam("file") MultipartFile file
    ) {
        String logoUrl = uploadService.uploadFile(file, "group-logos");
        return ResponseEntity.ok(groupService.updateGroupLogo(groupId, logoUrl));
    }
}