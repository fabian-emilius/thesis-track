package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.ThesisDto;
import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.service.GroupService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#groupId)")
    public ResponseEntity<GroupDto> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroup(groupId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<GroupDto> createGroup(@RequestBody @Valid CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request.getName(), request.getDescription()));
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("@groupSecurityEvaluator.isGroupAdmin(#groupId)")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable UUID groupId,
            @RequestBody @Valid UpdateGroupRequest request) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, request.getName(), request.getDescription()));
    }

    @GetMapping("/{groupId}/topics")
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#groupId)")
    public ResponseEntity<List<TopicDto>> getGroupTopics(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupTopics(groupId));
    }

    @GetMapping("/{groupId}/theses")
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#groupId)")
    public ResponseEntity<List<ThesisDto>> getGroupTheses(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupTheses(groupId));
    }

    @Data
    public static class CreateGroupRequest {
        @NotBlank
        @Size(max = 255)
        private String name;

        @Size(max = 1000)
        private String description;
    }

    @Data
    public static class UpdateGroupRequest {
        @NotBlank
        @Size(max = 255)
        private String name;

        @Size(max = 1000)
        private String description;
    }
}