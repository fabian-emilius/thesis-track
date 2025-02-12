package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.annotation.RateLimit;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.service.AuthenticationService;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UserService;
import de.tum.cit.aet.thesis.utility.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Endpoints for managing academic and research groups, including creation, updates, and member management")
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final AuthenticationService authService;

    /**
     * Retrieves all academic groups in the system.
     *
     * @return List of all groups with their details
     */
    @GetMapping
    @Operation(summary = "Get all groups", description = "Retrieves a list of all academic groups registered in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved groups"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    /**
     * Retrieves a specific group by its unique identifier.
     *
     * @param groupId The UUID of the group to retrieve
     * @return The group details
     * @throws ResourceNotFoundException if the group is not found
     */
    @GetMapping("/{groupId}")
    @Operation(summary = "Get group by ID", description = "Retrieves detailed information about a specific academic group")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Group found and returned successfully"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> getGroup(
            @Parameter(description = "UUID of the group to retrieve", required = true)
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    /**
     * Creates a new academic group. Only system administrators can create groups.
     *
     * @param groupDto The group information for creation
     * @return The created group details
     * @throws UnauthorizedException if the user is not a system administrator
     */
    @PostMapping
    @Operation(summary = "Create new group", description = "Creates a new academic group with the provided details. Requires system admin privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Group created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not system admin")
    })
    @RateLimit(limit = 10, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> createGroup(
            @Parameter(description = "Group details for creation", required = true)
            @Valid @RequestBody GroupDto groupDto) {
        authService.validateSystemAdmin();
        return ResponseEntity.ok(groupService.createGroup(groupDto, userService.getCurrentUser()));
    }

    /**
     * Updates an existing group's information. Only group administrators can update their groups.
     *
     * @param groupId The UUID of the group to update
     * @param groupDto The updated group information
     * @return The updated group details
     * @throws ResourceNotFoundException if the group is not found
     * @throws UnauthorizedException if the user is not a group administrator
     */
    @PutMapping("/{groupId}")
    @Operation(summary = "Update existing group", description = "Updates an existing academic group's information. Requires group admin privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Group updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not group admin"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @RateLimit(limit = 20, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> updateGroup(
            @Parameter(description = "UUID of the group to update", required = true)
            @PathVariable UUID groupId,
            @Parameter(description = "Updated group details", required = true)
            @Valid @RequestBody GroupDto groupDto) {
        authService.validateGroupAdmin(groupId);
        return ResponseEntity.ok(groupService.updateGroup(groupId, groupDto));
    }

    /**
     * Uploads or updates a group's logo image. Only group administrators can modify the logo.
     *
     * @param groupId The UUID of the group
     * @param file The logo image file (PNG or JPEG, max 5MB)
     * @return Empty response on success
     * @throws IOException if file processing fails
     * @throws ValidationException if file format or size is invalid
     * @throws UnauthorizedException if the user is not a group administrator
     */
    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload group logo", description = "Uploads or updates the logo image for a specific group. Accepts PNG or JPEG files up to 5MB.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logo uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "403", description = "Forbidden - user is not group admin"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @RateLimit(limit = 10, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Void> uploadGroupLogo(
            @Parameter(description = "UUID of the group", required = true)
            @PathVariable UUID groupId,
            @Parameter(description = "Logo image file (PNG/JPEG, max 5MB)", required = true)
            @RequestParam("file") MultipartFile file) throws IOException {
        authService.validateGroupAdmin(groupId);
        FileValidator.validateGroupLogo(file);
        groupService.updateGroupLogo(groupId, file.getBytes(), file.getContentType());
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves all members of a specific group with their roles and status.
     *
     * @param groupId The UUID of the group
     * @return List of group members with their details
     * @throws ResourceNotFoundException if the group is not found
     */
    @GetMapping("/{groupId}/members")
    @Operation(summary = "Get group members", description = "Retrieves a list of all members belonging to a specific academic group, including their roles and status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved group members"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions")
    })
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<List<GroupMember>> getGroupMembers(
            @Parameter(description = "UUID of the group", required = true)
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }
}
