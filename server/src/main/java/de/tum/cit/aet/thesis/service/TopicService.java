package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.TopicRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.TopicRoleRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicRoleRepository topicRoleRepository;
    private final GroupRepository groupRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public TopicService(TopicRepository topicRepository,
                       TopicRoleRepository topicRoleRepository,
                       GroupRepository groupRepository,
                       AuthenticationService authenticationService) {
        this.topicRepository = topicRepository;
        this.topicRoleRepository = topicRoleRepository;
        this.groupRepository = groupRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(readOnly = true)
    public Page<Topic> getTopics(UUID groupId, Pageable pageable) {
        return topicRepository.findByGroupIdAndClosedAtIsNull(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Topic> getTopicsByUser(UUID groupId, UUID userId, Pageable pageable) {
        return topicRepository.findByGroupIdAndUserIdAndClosedAtIsNull(groupId, userId, pageable);
    }

    @Transactional(readOnly = true)
    public Topic getTopicById(UUID id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        User currentUser = authenticationService.getCurrentUser();
        if (!topic.hasReadAccess(currentUser)) {
            throw new AccessDeniedException("No access to this topic");
        }

        return topic;
    }

    @Transactional
    public Topic createTopic(Topic topic, UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User currentUser = authenticationService.getCurrentUser();
        if (!currentUser.hasGroupRole(group, "ADMIN", "SUPERVISOR")) {
            throw new AccessDeniedException("No permission to create topics");
        }

        topic.setGroup(group);
        topic.setCreatedBy(currentUser);
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(UUID id, Topic updatedTopic) {
        Topic topic = getTopicById(id);

        User currentUser = authenticationService.getCurrentUser();
        if (!topic.hasEditAccess(currentUser)) {
            throw new AccessDeniedException("No permission to update this topic");
        }

        topic.setTitle(updatedTopic.getTitle());
        topic.setThesisTypes(updatedTopic.getThesisTypes());
        topic.setProblemStatement(updatedTopic.getProblemStatement());
        topic.setRequirements(updatedTopic.getRequirements());
        topic.setGoals(updatedTopic.getGoals());
        topic.setReferences(updatedTopic.getReferences());

        return topicRepository.save(topic);
    }

    @Transactional
    public Topic closeTopic(UUID id, String reason) {
        Topic topic = getTopicById(id);

        User currentUser = authenticationService.getCurrentUser();
        if (!topic.hasEditAccess(currentUser)) {
            throw new AccessDeniedException("No permission to close this topic");
        }

        topic.setClosedAt(Instant.now());
        return topicRepository.save(topic);
    }

    @Transactional
    public TopicRole addTopicRole(UUID topicId, UUID userId, String role, Integer position) {
        Topic topic = getTopicById(topicId);
        User user = authenticationService.getUserById(userId);

        User currentUser = authenticationService.getCurrentUser();
        if (!topic.hasEditAccess(currentUser)) {
            throw new AccessDeniedException("No permission to modify topic roles");
        }

        TopicRole topicRole = new TopicRole();
        topicRole.setTopic(topic);
        topicRole.setUser(user);
        topicRole.setRole(role);
        topicRole.setPosition(position);

        return topicRoleRepository.save(topicRole);
    }
}