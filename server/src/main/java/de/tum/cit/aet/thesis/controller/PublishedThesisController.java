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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{groupId}/published-theses")
@RequiredArgsConstructor
public class PublishedThesisController {
    private final ThesisService thesisService;

    @GetMapping
    public ResponseEntity<Page<Thesis>> getPublishedTheses(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Only return finished and public theses
        return ResponseEntity.ok(thesisService.searchTheses(
                groupId, type, ThesisState.FINISHED, searchQuery, pageRequest));
    }

    @GetMapping("/{thesisId}")
    public ResponseEntity<Thesis> getPublishedThesis(
            @PathVariable UUID groupId,
            @PathVariable UUID thesisId
    ) {
        Thesis thesis = thesisService.findById(thesisId);
        
        // Verify thesis is public and finished
        if (thesis.getState() != ThesisState.FINISHED || 
            thesis.getVisibility() != ThesisVisibility.PUBLIC) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(thesis);
    }
}