package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups")
public class GroupController {
    private final GroupService groupService;
    private final UploadService uploadService;

    @Autowired
    public GroupController(GroupService groupService, UploadService uploadService) {
        this.groupService = groupService;
        this.uploadService = uploadService;
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasRole('ADMIN') or @groupPermissionEvaluator.hasGroupRole(#groupId, 'ADMIN')")
    public ResponseEntity<Group> updateGroup(@PathVariable UUID groupId, @RequestBody Group group) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, group));
    }

    @PutMapping("/{groupId}/settings")
    @PreAuthorize("hasRole('ADMIN') or @groupPermissionEvaluator.hasGroupRole(#groupId, 'ADMIN')")
    public ResponseEntity<Group> updateGroupSettings(
            @PathVariable UUID groupId,
            @RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(groupService.updateGroupSettings(groupId, settings));
    }

    @PostMapping("/{groupId}/logo")
    @PreAuthorize("hasRole('ADMIN') or @groupPermissionEvaluator.hasGroupRole(#groupId, 'ADMIN')")
    public ResponseEntity<Group> uploadGroupLogo(
            @PathVariable UUID groupId,
            @RequestParam("file") MultipartFile file) {
        String logoUrl = uploadService.uploadFile(file, "group-logos");
        return ResponseEntity.ok(groupService.updateGroupLogo(groupId, logoUrl));
    }
}