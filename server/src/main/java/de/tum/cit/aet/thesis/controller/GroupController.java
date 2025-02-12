package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.annotation.RateLimit;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UserService;
import de.tum.cit.aet.thesis.utility.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Groups", description = "Group management endpoints")
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all groups")
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "Get group by ID")
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping
    @Operation(summary = "Create new group")
    @RateLimit(limit = 10, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> createGroup(@Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.createGroup(groupDto, userService.getCurrentUser()));
    }

    @PutMapping("/{groupId}")
    @Operation(summary = "Update existing group")
    @RateLimit(limit = 20, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, groupDto));
    }

    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload group logo")
    @RateLimit(limit = 10, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<Void> uploadGroupLogo(
            @PathVariable UUID groupId,
            @RequestParam("file") MultipartFile file) throws IOException {
        FileValidator.validateGroupLogo(file);
        groupService.updateGroupLogo(groupId, file.getBytes(), file.getContentType());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "Get group members")
    @RateLimit(limit = 100, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }
}
