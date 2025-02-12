package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.service.GroupService;
import de.tum.cit.aet.thesis.service.UserService;
import de.tum.cit.aet.thesis.util.FileValidator;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
public class GroupController {
    private final Bucket bucket;
    private final GroupService groupService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(429).build();
        }
        log.debug("Fetching all groups");
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@Valid @RequestBody GroupDto groupDto) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(429).build();
        }
        log.debug("Creating new group: {}", groupDto.getName());
        return ResponseEntity.ok(groupService.createGroup(groupDto, userService.getCurrentUser()));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupDto groupDto) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(429).build();
        }
        log.debug("Updating group {}: {}", groupId, groupDto.getName());
        return ResponseEntity.ok(groupService.updateGroup(groupId, groupDto));
    }

    @PostMapping(value = "/{groupId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadGroupLogo(
            @PathVariable UUID groupId,
            @RequestParam("file") MultipartFile file) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(429).build();
        }
        try {
            if (!FileValidator.isValidImage(file)) {
                log.warn("Invalid file type attempted for group logo upload: {}", file.getContentType());
                return ResponseEntity.badRequest().build();
            }
            log.debug("Uploading logo for group {}", groupId);
            groupService.updateGroupLogo(groupId, file.getBytes(), file.getContentType());
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Error processing group logo upload for group {}: {}", groupId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupMembers(groupId));
    }
}
