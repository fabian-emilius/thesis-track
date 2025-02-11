package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.service.TopicService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v2/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<TopicDto>> getOpenTopics(Pageable pageable) {
        return ResponseEntity.ok(topicService.getOpenTopics(pageable));
    }

    @PostMapping
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#request.groupId)")
    public ResponseEntity<TopicDto> createTopic(@RequestBody @Valid CreateTopicRequest request) {
        return ResponseEntity.ok(topicService.createTopic(
                request.getGroupId(),
                request.getTitle(),
                request.getThesisTypes(),
                request.getProblemStatement(),
                request.getRequirements(),
                request.getGoals(),
                request.getReferences()
        ));
    }

    @PutMapping("/{topicId}")
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#topicId)")
    public ResponseEntity<TopicDto> updateTopic(
            @PathVariable UUID topicId,
            @RequestBody @Valid UpdateTopicRequest request) {
        return ResponseEntity.ok(topicService.updateTopic(
                topicId,
                request.getTitle(),
                request.getThesisTypes(),
                request.getProblemStatement(),
                request.getRequirements(),
                request.getGoals(),
                request.getReferences()
        ));
    }

    @PostMapping("/{topicId}/close")
    @PreAuthorize("@groupSecurityEvaluator.hasGroupAccess(#topicId)")
    public ResponseEntity<TopicDto> closeTopic(
            @PathVariable UUID topicId,
            @RequestBody @Valid CloseTopicRequest request) {
        return ResponseEntity.ok(topicService.closeTopic(topicId, request.getReason()));
    }

    @Data
    public static class CreateTopicRequest {
        @NotNull
        private UUID groupId;

        @NotBlank
        @Size(max = 255)
        private String title;

        private Set<String> thesisTypes;

        @NotBlank
        private String problemStatement;

        @NotBlank
        private String requirements;

        @NotBlank
        private String goals;

        @NotBlank
        private String references;
    }

    @Data
    public static class UpdateTopicRequest {
        @NotBlank
        @Size(max = 255)
        private String title;

        private Set<String> thesisTypes;

        @NotBlank
        private String problemStatement;

        @NotBlank
        private String requirements;

        @NotBlank
        private String goals;

        @NotBlank
        private String references;
    }

    @Data
    public static class CloseTopicRequest {
        @NotBlank
        private String reason;
    }
}