package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.service.GroupMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {
    private final GroupMemberService groupMemberService;

    @GetMapping
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupMember(#groupId)")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupMemberService.getGroupMembers(groupId));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupAdmin(#groupId)")
    public ResponseEntity<GroupMemberDto> addGroupMember(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestParam GroupRole role) {
        return ResponseEntity.ok(groupMemberService.addGroupMember(groupId, userId, role));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupAdmin(#groupId)")
    public ResponseEntity<Void> removeGroupMember(
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupMemberService.removeGroupMember(groupId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('admin') or @accessManagementService.isGroupAdmin(#groupId)")
    public ResponseEntity<GroupMemberDto> updateMemberRole(
            @PathVariable UUID groupId,
            @PathVariable UUID userId,
            @RequestParam GroupRole role) {
        return ResponseEntity.ok(groupMemberService.updateMemberRole(groupId, userId, role));
    }
}