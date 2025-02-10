package de.tum.cit.aet.thesis.controller;

import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.service.TopicGroupService;
import de.tum.cit.aet.thesis.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final TopicGroupService topicGroupService;

    @GetMapping("/groups/{groupId}/topics")
    @PreAuthorize("@groupSecurityService.hasGroupAccess(#groupId)")
    public ResponseEntity<List<Topic>> getTopicsByGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(topicGroupService.getTopicsByGroup(groupId));
    }

    @PostMapping("/topics/{topicId}/group")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> assignTopicToGroup(
            @PathVariable UUID topicId,
            @RequestParam UUID groupId) {
        topicGroupService.assignTopicToGroup(topicId, groupId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/topics/{topicId}/group")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> removeTopicFromGroup(@PathVariable UUID topicId) {
        topicGroupService.removeTopicFromGroup(topicId);
        return ResponseEntity.ok().build();
    }
}