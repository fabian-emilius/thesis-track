package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Group Management", description = "APIs for managing groups")
public class GroupController {
    private final GroupService groupService;
    private final UploadService uploadService;

    @GetMapping
    @Operation(summary = "Get all groups", description = "Retrieves a list of all available groups")
    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully")
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'MEMBER')")
    @Operation(summary = "Get group by ID", description = "Retrieves a specific group by its ID")
    @ApiResponse(responseCode = "200", description = "Group retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> getGroup(
            @Parameter(description = "ID of the group to retrieve")
            @PathVariable UUID groupId
    ) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create new group", description = "Creates a new group in the system")
    @ApiResponse(responseCode = "200", description = "Group created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid group data")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    @Operation(summary = "Update group", description = "Updates an existing group's details")
    @ApiResponse(responseCode = "200", description = "Group updated successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> updateGroup(
            @Parameter(description = "ID of the group to update")
            @PathVariable UUID groupId,
            @Valid @RequestBody Group group
    ) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, group));
    }

    @PutMapping("/{groupId}/settings")
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    @Operation(summary = "Update group settings", description = "Updates a group's settings")
    @ApiResponse(responseCode = "200", description = "Settings updated successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> updateGroupSettings(
            @Parameter(description = "ID of the group to update")
            @PathVariable UUID groupId,
            @RequestBody Map<String, Object> settings
    ) {
        return ResponseEntity.ok(groupService.updateGroupSettings(groupId, settings));
    }

    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasPermission(#groupId, 'Group', 'ADMIN')")
    @Operation(summary = "Upload group logo", description = "Uploads a new logo for the group")
    @ApiResponse(responseCode = "200", description = "Logo uploaded successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> uploadGroupLogo(
            @Parameter(description = "ID of the group")
            @PathVariable UUID groupId,
            @Parameter(description = "Logo file to upload")
            @RequestParam("file") MultipartFile file
    ) {
        String logoUrl = uploadService.uploadFile(file, "group-logos");
        return ResponseEntity.ok(groupService.updateGroupLogo(groupId, logoUrl));
    }
}