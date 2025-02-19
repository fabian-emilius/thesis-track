package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.controller.payload.AddGroupMemberPayload;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.GroupMemberService;
import de.tum.cit.aet.thesis.service.GroupBasedAccessService;
import de.tum.cit.aet.thesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{slug}/members")
@RequiredArgsConstructor
public class GroupMemberController {
    private final GroupService groupService;
    private final GroupMemberService memberService;
    private final GroupBasedAccessService accessService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable String slug) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.hasGroupAccess(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        return ResponseEntity.ok(memberService.getGroupMembers(group.getId()).stream()
                .map(GroupMemberDto::fromEntity)
                .toList());
    }

    @PostMapping
    public ResponseEntity<GroupMemberDto> addGroupMember(
            @PathVariable String slug,
            @Valid @RequestBody AddGroupMemberPayload payload
    ) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.canManageGroup(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        User user = userService.getUserById(payload.userId());
        return ResponseEntity.ok(GroupMemberDto.fromEntity(
                memberService.addMember(group, user, payload.role())
        ));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeGroupMember(
            @PathVariable String slug,
            @PathVariable UUID userId
    ) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.canManageGroup(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        memberService.removeMember(group.getId(), userId);
        return ResponseEntity.ok().build();
    }
}