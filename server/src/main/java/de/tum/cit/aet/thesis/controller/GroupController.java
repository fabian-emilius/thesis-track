package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
@Validated
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Group>> getAllGroups() {
        log.debug("Retrieving all groups");
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Group> getGroupById(@PathVariable UUID id) {
        log.debug("Retrieving group with id: {}", id);
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Group> createGroup(@Valid @RequestBody Group group) {
        log.debug("Creating new group: {}", group.getName());
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Group> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody Group group) {
        log.debug("Updating group with id: {}", id);
        return ResponseEntity.ok(groupService.updateGroup(id, group));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        log.debug("Deleting group with id: {}", id);
        groupService.deleteGroup(id);
        return ResponseEntity.ok().build();
    }
}