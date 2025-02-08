package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.TopicRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.TopicRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicRoleRepository topicRoleRepository;
    private final UserService userService;
    private final GroupService groupService;

    @Transactional(readOnly = true)
    public Page<Topic> searchTopics(
            Long groupId,
            String searchQuery,
            String thesisType,
            Pageable pageable
    ) {
        return topicRepository.searchTopics(
                groupId,
                searchQuery != null ? searchQuery.toLowerCase() : null,
                thesisType,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Topic findById(UUID id, Long groupId) {
        return topicRepository.findByIdAndGroupId(id, groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    @Transactional
    public Topic createTopic(
            User createdBy,
            String title,
            Set<String> thesisTypes,
            String problemStatement,
            String requirements,
            String goals,
            String references,
            List<UUID> supervisorIds,
            List<UUID> advisorIds,
            Long groupId
    ) {
        Group group = groupService.findById(groupId);
        validateRoles(supervisorIds, advisorIds);

        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setThesisTypes(thesisTypes);
        topic.setProblemStatement(problemStatement);
        topic.setRequirements(requirements);
        topic.setGoals(goals);
        topic.setReferences(references);
        topic.setCreatedBy(createdBy);
        topic.setGroup(group);
        topic.setCreatedAt(Instant.now());

        topic = topicRepository.save(topic);

        addRoles(topic, supervisorIds, "SUPERVISOR");
        if (advisorIds != null) {
            addRoles(topic, advisorIds, "ADVISOR");
        }

        return topic;
    }

    private void validateRoles(List<UUID> supervisorIds, List<UUID> advisorIds) {
        if (supervisorIds == null || supervisorIds.isEmpty()) {
            throw new ResourceInvalidParametersException("At least one supervisor is required");
        }

        // Validate that users exist and have appropriate roles
        supervisorIds.forEach(id -> {
            User user = userService.findById(id);
            if (!user.hasAnyGroup("supervisor")) {
                throw new ResourceInvalidParametersException("User " + user.getEmail() + " is not a supervisor");
            }
        });

        if (advisorIds != null) {
            advisorIds.forEach(id -> {
                User user = userService.findById(id);
                if (!user.hasAnyGroup("advisor")) {
                    throw new ResourceInvalidParametersException("User " + user.getEmail() + " is not an advisor");
                }
            });
        }
    }

    private void addRoles(Topic topic, List<UUID> userIds, String role) {
        int position = 0;
        for (UUID userId : userIds) {
            User user = userService.findById(userId);
            TopicRole topicRole = new TopicRole();
            topicRole.setTopic(topic);
            topicRole.setUser(user);
            topicRole.setPosition(position++);
            topicRole.getId().setRole(role);
            topicRoleRepository.save(topicRole);
        }
    }

    // ... rest of the methods remain the same ...
}