package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.service.TopicService;
import de.tum.cit.aet.thesis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v2/groups/{groupId}/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("@accessManagementService.isGroupMember(#groupId)")
    public ResponseEntity<Page<Topic>> getTopics(
            @PathVariable UUID groupId,
            @RequestParam(required = false) String[] types,
            @RequestParam(defaultValue = "false") boolean includeClosed,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        return ResponseEntity.ok(topicService.getAll(
                groupId, types, includeClosed, searchQuery,
                page, limit, sortBy, sortOrder
        ));
    }

    @GetMapping("/{topicId}")
    @PreAuthorize("@accessManagementService.isGroupMember(#groupId)")
    public ResponseEntity<Topic> getTopic(
            @PathVariable UUID groupId,
            @PathVariable UUID topicId
    ) {
        return ResponseEntity.ok(topicService.findById(topicId));
    }

    @PostMapping
    @PreAuthorize("@accessManagementService.isGroupSupervisor(#groupId)")
    public ResponseEntity<Topic> createTopic(
            @PathVariable UUID groupId,
            @RequestParam String title,
            @RequestParam Set<String> thesisTypes,
            @RequestParam String problemStatement,
            @RequestParam String requirements,
            @RequestParam String goals,
            @RequestParam String references,
            @RequestParam List<UUID> supervisorIds,
            @RequestParam List<UUID> advisorIds
    ) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(topicService.createTopic(
                groupId, currentUser, title, thesisTypes,
                problemStatement, requirements, goals, references,
                supervisorIds, advisorIds
        ));
    }

    @PutMapping("/{topicId}")
    @PreAuthorize("@accessManagementService.isGroupSupervisor(#groupId)")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable UUID groupId,
            @PathVariable UUID topicId,
            @RequestParam String title,
            @RequestParam Set<String> thesisTypes,
            @RequestParam String problemStatement,
            @RequestParam String requirements,
            @RequestParam String goals,
            @RequestParam String references,
            @RequestParam List<UUID> supervisorIds,
            @RequestParam List<UUID> advisorIds
    ) {
        Topic topic = topicService.findById(topicId);
        User currentUser = userService.getCurrentUser();

        return ResponseEntity.ok(topicService.updateTopic(
                currentUser, topic, title, thesisTypes,
                problemStatement, requirements, goals, references,
                supervisorIds, advisorIds
        ));
    }
}