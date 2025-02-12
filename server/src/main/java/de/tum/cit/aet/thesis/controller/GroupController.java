package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.createGroup(groupDto, userService.getCurrentUser()));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, groupDto));
    }

    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadGroupLogo(
            @PathVariable UUID groupId,
            @RequestParam("file") MultipartFile file) throws IOException {
        groupService.updateGroupLogo(groupId, file.getBytes(), file.getContentType());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }
}
