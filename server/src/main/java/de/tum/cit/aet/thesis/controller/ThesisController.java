package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.constants.ThesisState;
import de.tum.cit.aet.thesis.constants.ThesisVisibility;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.service.ThesisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{groupId}/theses")
@RequiredArgsConstructor
public class ThesisController {
    private final ThesisService thesisService;

    @GetMapping
    @PreAuthorize("@accessManagementService.isGroupMember(#groupId)")
    public ResponseEntity<Page<Thesis>> searchTheses(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) ThesisState state,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(thesisService.searchTheses(
                groupId, type, state, searchQuery, pageRequest));
    }

    @GetMapping("/{thesisId}")
    @PreAuthorize("@accessManagementService.isGroupMember(#groupId)")
    public ResponseEntity<Thesis> getThesis(
            @PathVariable UUID groupId,
            @PathVariable UUID thesisId
    ) {
        return ResponseEntity.ok(thesisService.findById(thesisId));
    }

    @PostMapping
    @PreAuthorize("@accessManagementService.isGroupAdvisor(#groupId)")
    public ResponseEntity<Thesis> createThesis(
            @PathVariable UUID groupId,
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String language,
            @RequestParam String info,
            @RequestParam String abstractText,
            @RequestParam Set<String> keywords,
            @RequestParam ThesisVisibility visibility,
            @RequestParam UUID supervisorId,
            @RequestParam List<UUID> advisorIds,
            @RequestParam UUID studentId
    ) {
        return ResponseEntity.ok(thesisService.createThesis(
                groupId, title, type, language, info, abstractText,
                keywords, visibility, supervisorId, advisorIds, studentId
        ));
    }

    @PutMapping("/{thesisId}")
    @PreAuthorize("@accessManagementService.isGroupAdvisor(#groupId)")
    public ResponseEntity<Thesis> updateThesis(
            @PathVariable UUID groupId,
            @PathVariable UUID thesisId,
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam String language,
            @RequestParam String info,
            @RequestParam String abstractText,
            @RequestParam Set<String> keywords,
            @RequestParam ThesisVisibility visibility,
            @RequestParam UUID supervisorId,
            @RequestParam List<UUID> advisorIds,
            @RequestParam UUID studentId
    ) {
        Thesis thesis = thesisService.findById(thesisId);
        return ResponseEntity.ok(thesisService.updateThesis(
                thesis, title, type, language, info, abstractText,
                keywords, visibility, supervisorId, advisorIds, studentId
        ));
    }
}