package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.service.GroupService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing research groups.
 * Provides endpoints for CRUD operations on groups and group membership management.
 * All administrative operations require admin role and are rate-limited.
 */
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {
    private final GroupService groupService;
    private final Bucket groupManagementBucket;

    /**
     * Checks if the current request is within rate limits.
     * @return ResponseEntity with TOO_MANY_REQUESTS status if rate limit exceeded, null otherwise
     */
    private ResponseEntity<?> checkRateLimit() {
        return !groupManagementBucket.tryConsume(1)
            ? ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                    String.valueOf(groupManagementBucket.getConfiguration().getRefillPeriod().getSeconds()))
                .build()
            : null;
    }

    @Operation(summary = "Get all research groups", description = "Retrieves a list of all research groups")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved groups list")
    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @Operation(summary = "Get group by ID", description = "Retrieves a specific research group by its UUID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group found"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroupById(
            @Parameter(description = "UUID of the group to retrieve")
            @PathVariable @NotNull UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @Operation(summary = "Get group by slug", description = "Retrieves a specific research group by its URL-friendly identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group found"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<GroupDto> getGroupBySlug(
            @Parameter(description = "URL-friendly identifier of the group")
            @PathVariable @NotBlank @Pattern(regexp = "^[a-z0-9-]+$") String slug) {
        return ResponseEntity.ok(groupService.getGroupBySlug(slug));
    }

    @Operation(summary = "Create new research group", description = "Creates a new research group with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupDto> createGroup(
            @Parameter(description = "Name of the group")
            @RequestParam @NotBlank @Size(min = 2, max = 100) String name,
            @Parameter(description = "URL-friendly identifier for the group")
            @RequestParam @NotBlank @Pattern(regexp = "^[a-z0-9-]+$") @Size(min = 2, max = 50) String slug,
            @Parameter(description = "Description of the group")
            @RequestParam(required = false) @Size(max = 1000) String description,
            @Parameter(description = "URL of the group's logo")
            @RequestParam(required = false) @Size(max = 255) String logoUrl,
            @Parameter(description = "External website link")
            @RequestParam(required = false) @Size(max = 255) String externalLink) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        return ResponseEntity.ok(groupService.createGroup(name, slug, description, logoUrl, externalLink));
    }

    @Operation(summary = "Update group details", description = "Updates the basic information of a research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupDto> updateGroup(
            @Parameter(description = "UUID of the group to update")
            @PathVariable @NotNull UUID groupId,
            @Parameter(description = "New name for the group")
            @RequestParam @NotBlank @Size(min = 2, max = 100) String name,
            @Parameter(description = "New description for the group")
            @RequestParam(required = false) @Size(max = 1000) String description,
            @Parameter(description = "New logo URL for the group")
            @RequestParam(required = false) @Size(max = 255) String logoUrl,
            @Parameter(description = "New external website link")
            @RequestParam(required = false) @Size(max = 255) String externalLink) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        return ResponseEntity.ok(groupService.updateGroup(groupId, name, description, logoUrl, externalLink));
    }

    @Operation(summary = "Update group settings", description = "Updates the settings for a specific research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Settings updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid settings data"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PutMapping("/{groupId}/settings")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupSettingsDto> updateGroupSettings(
            @Parameter(description = "UUID of the group to update")
            @PathVariable @NotNull UUID groupId,
            @Parameter(description = "New settings for the group")
            @RequestBody @Valid GroupSettingsDto settings) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        return ResponseEntity.ok(groupService.updateGroupSettings(groupId, settings));
    }

    @Operation(summary = "Delete group", description = "Deletes a research group and all associated data")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Group deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Group not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "UUID of the group to delete")
            @PathVariable @NotNull UUID groupId) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get group members", description = "Retrieves a list of all members in a research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Members list retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Group not found")
    })
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(
            @Parameter(description = "UUID of the group")
            @PathVariable @NotNull UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }

    @Operation(summary = "Add member to group", description = "Adds a new member to a research group with specified role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Member added successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Group or user not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @PostMapping("/{groupId}/members")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupMemberDto> addGroupMember(
            @Parameter(description = "UUID of the group")
            @PathVariable @NotNull UUID groupId,
            @Parameter(description = "UUID of the user to add")
            @RequestParam @NotNull UUID userId,
            @Parameter(description = "Role to assign to the user")
            @RequestParam @NotBlank @Pattern(regexp = "^(ADMIN|MEMBER)$") String role) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        return ResponseEntity.ok(groupService.addGroupMember(groupId, userId, role));
    }

    @Operation(summary = "Remove group member", description = "Removes a member from a research group")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Member removed successfully"),
        @ApiResponse(responseCode = "404", description = "Group or member not found"),
        @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    @DeleteMapping("/{groupId}/members/{userId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> removeGroupMember(
            @Parameter(description = "UUID of the group")
            @PathVariable @NotNull UUID groupId,
            @Parameter(description = "UUID of the user to remove")
            @PathVariable @NotNull UUID userId) {
        ResponseEntity<?> rateLimitCheck = checkRateLimit();
        if (rateLimitCheck != null) return rateLimitCheck;
        groupService.removeGroupMember(groupId, userId);
        return ResponseEntity.ok().build();
    }
}
