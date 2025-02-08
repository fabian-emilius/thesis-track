package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.controller.payload.CreateGroupPayload;
import de.tum.cit.aet.thesis.controller.payload.UpdateGroupPayload;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody CreateGroupPayload payload) {
        return ResponseEntity.ok(groupService.createGroup(payload.getName(), payload.getDescription()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody UpdateGroupPayload payload
    ) {
        return ResponseEntity.ok(groupService.updateGroup(id, payload.getName(), payload.getDescription()));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deactivateGroup(@PathVariable Long id) {
        groupService.deactivateGroup(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> activateGroup(@PathVariable Long id) {
        groupService.activateGroup(id);
        return ResponseEntity.ok().build();
    }
}