package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing groups.
 * Provides endpoints for creating, updating, and retrieving group information.
 */
@RestController
@RequestMapping("/v2/groups")
@Tag(name = "Group Management", description = "APIs for managing thesis groups")
@Validated
@Slf4j
public class GroupController {
    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * GET /v2/groups : Get all available groups
     * @return List of groups
     */
    @GetMapping
    @Operation(summary = "Get all groups", description = "Returns a list of all available groups")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved groups")
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        log.debug("REST request to get all Groups");
        List<GroupDto> groups = groupService.getAllGroups().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groups);
    }

    /**
     * GET /v2/groups/{id} : Get group by id
     * @param id the id of the group to retrieve
     * @return the ResponseEntity with status 200 (OK) and the group in body
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a group by id")
    @ApiResponse(responseCode = "200", description = "Group found")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<GroupDto> getGroup(
            @Parameter(description = "ID of the group", required = true)
            @PathVariable UUID id) {
        log.debug("REST request to get Group : {}", id);
        Group group = groupService.getGroupById(id);
        return ResponseEntity.ok(convertToDto(group));
    }

    /**
     * POST /v2/groups : Create a new group
     * @param groupDto the group to create
     * @return the ResponseEntity with status 200 (OK) and the new group in body
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new group")
    @ApiResponse(responseCode = "200", description = "Group created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Not authorized to create groups")
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
        log.debug("REST request to create Group : {}", groupDto);
        Group group = new Group();
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        
        Group savedGroup = groupService.createGroup(group);
        return ResponseEntity.ok(convertToDto(savedGroup));
    }

    /**
     * PUT /v2/groups/{id} : Update an existing group
     * @param id the id of the group to update
     * @param groupDto the group to update
     * @return the ResponseEntity with status 200 (OK) and the updated group in body
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing group")
    @ApiResponse(responseCode = "200", description = "Group updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Not authorized to update groups")
    @ApiResponse(responseCode = "404", description = "Group not found")
    public ResponseEntity<GroupDto> updateGroup(
            @Parameter(description = "ID of the group to update", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody GroupDto groupDto) {
        log.debug("REST request to update Group : {}", groupDto);
        Group group = new Group();
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        
        Group updatedGroup = groupService.updateGroup(id, group);
        return ResponseEntity.ok(convertToDto(updatedGroup));
    }

    /**
     * Converts a Group entity to a GroupDto
     * @param group the entity to convert
     * @return the converted DTO
     */
    private GroupDto convertToDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }
}
