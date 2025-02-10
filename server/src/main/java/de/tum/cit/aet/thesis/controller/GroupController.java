package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
@Tag(name = "Group Management", description = "APIs for managing groups")
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all groups", description = "Retrieves all available groups. Requires admin role.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        log.debug("Retrieving all groups");
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get group by ID", description = "Retrieves a specific group by its ID. Requires admin role.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> getGroupById(
            @Parameter(description = "ID of the group to retrieve") 
            @PathVariable UUID id) {
        log.debug("Retrieving group with id: {}", id);
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create new group", description = "Creates a new group. Requires admin role.")
    @ApiResponse(responseCode = "200", description = "Successfully created group")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<Group> createGroup(
            @Parameter(description = "Group details") 
            @Valid @RequestBody Group group) {
        log.debug("Creating new group: {}", group.getName());
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update group", description = "Updates an existing group. Requires admin role.")
    @ApiResponse(responseCode = "200", description = "Successfully updated group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Group> updateGroup(
            @Parameter(description = "ID of the group to update") 
            @PathVariable UUID id,
            @Parameter(description = "Updated group details") 
            @Valid @RequestBody Group group) {
        log.debug("Updating group with id: {}", id);
        return ResponseEntity.ok(groupService.updateGroup(id, group));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete group", description = "Deletes an existing group. Requires admin role.")
    @ApiResponse(responseCode = "200", description = "Successfully deleted group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<Void> deleteGroup(
            @Parameter(description = "ID of the group to delete") 
            @PathVariable UUID id) {
        log.debug("Deleting group with id: {}", id);
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}