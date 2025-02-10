package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.PaginationDto;
import de.tum.cit.aet.thesis.dto.PublishedThesisDto;
import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.service.PublishedThesisService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v2/published-theses")
@RequiredArgsConstructor
public class PublishedThesisController {
    private final PublishedThesisService publishedThesisService;

    @GetMapping
    public PaginationDto<PublishedThesisDto> getVisibleTheses(
            @RequestParam UUID groupId,
            Pageable pageable
    ) {
        Page<PublishedThesis> theses = publishedThesisService.getVisibleTheses(groupId, pageable);
        return PaginationDto.from(theses.map(PublishedThesisDto::from));
    }

    @PostMapping("/{thesisId}/visibility")
    @PreAuthorize("hasRole('admin')")
    public PublishedThesisDto updateVisibility(
            @PathVariable UUID thesisId,
            @RequestBody UpdateVisibilityRequest request
    ) {
        PublishedThesis thesis = publishedThesisService.updateVisibilityGroups(thesisId, request.getVisibilityGroups());
        return PublishedThesisDto.from(thesis);
    }

    @DeleteMapping("/{thesisId}")
    @PreAuthorize("hasRole('admin')")
    public void unpublishThesis(@PathVariable UUID thesisId) {
        publishedThesisService.unpublishThesis(thesisId);
    }

    @Data
    public static class UpdateVisibilityRequest {
        private Set<UUID> visibilityGroups;
    }
}
