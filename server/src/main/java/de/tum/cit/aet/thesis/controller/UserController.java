package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.UserGroupDto;
import de.tum.cit.aet.thesis.dto.PaginationDto;
import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.service.UserService;
import lombok.Data;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
@Tag(name = "User Groups", description = "APIs for managing user groups")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all user groups", description = "Retrieves a paginated list of all user groups")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved groups")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PaginationDto<UserGroupDto>> getAllGroups(Pageable pageable) {
        Page<UserGroup> groups = userService.getAllGroups(pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(PaginationDto.from(groups.map(UserGroupDto::from)));
    }

    @Operation(summary = "Get topics for a group", description = "Retrieves a paginated list of topics associated with a group")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved topics")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/{groupId}/topics")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<PaginationDto<TopicDto>> getGroupTopics(
            @PathVariable UUID groupId,
            Pageable pageable
    ) {
        Page<Topic> topics = userService.getGroupTopics(groupId, pageable);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(PaginationDto.from(topics.map(TopicDto::from)));
    }

    @Operation(summary = "Get a user group by ID", description = "Retrieves details of a specific user group")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved group")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<UserGroupDto> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(UserGroupDto.from(userService.getGroupById(groupId)));
    }

    @Operation(summary = "Create a new user group", description = "Creates a new user group with the provided details")
    @ApiResponse(responseCode = "200", description = "Group successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<UserGroupDto> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        UserGroup group = userService.createGroup(request.getName(), request.getDescription());
        return ResponseEntity.ok(UserGroupDto.from(group));
    }

    @Operation(summary = "Update a user group", description = "Updates an existing user group with the provided details")
    @ApiResponse(responseCode = "200", description = "Group successfully updated")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<UserGroupDto> updateGroup(
            @PathVariable UUID groupId,
            @RequestBody UpdateGroupRequest request
    ) {
        UserGroup group = userService.updateGroup(groupId, request.getName(), request.getDescription());
        return ResponseEntity.ok(UserGroupDto.from(group));
    }

    @Data
    public static class CreateGroupRequest {
        @NotBlank(message = "Group name is required")
        @Size(min = 3, max = 50, message = "Group name must be between 3 and 50 characters")
        private String name;
        
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        private String description;
    }

    @Data
    public static class UpdateGroupRequest {
        @NotBlank(message = "Group name is required")
        @Size(min = 3, max = 50, message = "Group name must be between 3 and 50 characters")
        private String name;
        
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        private String description;
    }
}
