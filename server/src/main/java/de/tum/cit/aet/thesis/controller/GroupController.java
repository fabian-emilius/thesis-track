package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.controller.payload.CreateGroupPayload;
import de.tum.cit.aet.thesis.controller.payload.UpdateGroupPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Research Groups", description = "APIs for managing research groups")
public class GroupController {
    private final GroupService groupService;
    private final GroupBasedAccessService accessService;
    private final AuthenticationService authService;

    @Operation(summary = "Get all research groups", description = "Retrieves a list of all research groups")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved research groups"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<List<ResearchGroupDto>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups().stream()
                .map(ResearchGroupDto::fromEntity)
                .toList());
    }

    @Operation(summary = "Get research group by slug", description = "Retrieves a specific research group using its slug")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved research group"),
        @ApiResponse(responseCode = "404", description = "Research group not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<ResearchGroupDto> getGroup(
            @Parameter(description = "Slug of the research group", required = true) @PathVariable String slug) {
        ResearchGroup group = groupService.getGroupBySlug(slug);
        return ResponseEntity.ok(ResearchGroupDto.fromEntity(group));
    }

    @Operation(summary = "Create new research group", description = "Creates a new research group. Requires admin privileges")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully created research group"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires admin role")
    })
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

    @Operation(summary = "Update research group", description = "Updates an existing research group. Requires group management permissions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully updated research group"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Research group not found")
    })
    @PutMapping("/{slug}")
    public ResponseEntity<ResearchGroupDto> updateGroup(
            @Parameter(description = "Slug of the research group to update", required = true) @PathVariable String slug,
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