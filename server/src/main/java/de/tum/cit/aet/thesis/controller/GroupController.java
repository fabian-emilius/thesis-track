package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.controller.payload.CreateGroupPayload;
import de.tum.cit.aet.thesis.controller.payload.UpdateGroupPayload;
import de.tum.cit.aet.thesis.dto.ResearchGroupDto;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.service.AuthenticationService;
import de.tum.cit.aet.thesis.service.GroupBasedAccessService;
import de.tum.cit.aet.thesis.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v2/groups")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final GroupBasedAccessService accessService;
    private final AuthenticationService authService;

    @GetMapping
    public ResponseEntity<List<ResearchGroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups().stream()
                .map(ResearchGroupDto::fromEntity)
                .toList());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResearchGroupDto> getGroup(@PathVariable String slug) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        return ResponseEntity.ok(ResearchGroupDto.fromEntity(group));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<ResearchGroupDto> createGroup(@Valid @RequestBody CreateGroupPayload payload) {
        ResearchGroup group = new ResearchGroup();
        group.setName(payload.name());
        group.setSlug(payload.slug());
        group.setDescription(payload.description());
        group.setWebsiteLink(payload.websiteLink());
        group.setMailFooter(payload.mailFooter());
        group.setAcceptanceEmailText(payload.acceptanceEmailText());
        group.setAcceptanceInstructions(payload.acceptanceInstructions());

        ResearchGroup created = groupService.createGroup(group, authService.getCurrentUser());
        return ResponseEntity.ok(ResearchGroupDto.fromEntity(created));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<ResearchGroupDto> updateGroup(@PathVariable String slug,
            @Valid @RequestBody UpdateGroupPayload payload
    ) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        if (!accessService.canManageGroup(group.getId())) {
            return ResponseEntity.forbidden().build();
        }

        ResearchGroup updatedGroup = new ResearchGroup();
        updatedGroup.setName(payload.name());
        updatedGroup.setDescription(payload.description());
        updatedGroup.setWebsiteLink(payload.websiteLink());
        updatedGroup.setMailFooter(payload.mailFooter());
        updatedGroup.setAcceptanceEmailText(payload.acceptanceEmailText());
        updatedGroup.setAcceptanceInstructions(payload.acceptanceInstructions());

        ResearchGroup saved = groupService.updateGroup(slug, updatedGroup);
        return ResponseEntity.ok(ResearchGroupDto.fromEntity(saved));
    }
}