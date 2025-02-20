package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.service.GroupService;
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
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable UUID id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<GroupDto> getGroupBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(groupService.getGroupBySlug(slug));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupDto> createGroup(@RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.createGroup(groupDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupAdmin(#id)")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable UUID id,
            @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupDto));
    }

    @PutMapping("/{id}/settings")
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupAdmin(#id)")
    public ResponseEntity<GroupSettingsDto> updateGroupSettings(
            @PathVariable UUID id,
            @RequestBody GroupSettingsDto settingsDto) {
        return ResponseEntity.ok(groupService.updateGroupSettings(id, settingsDto));
    }
}