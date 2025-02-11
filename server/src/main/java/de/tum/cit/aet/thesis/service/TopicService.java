package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TopicService extends BaseGroupService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository,
                       GroupRepository groupRepository,
                       UserGroupRepository userGroupRepository,
                       AuthenticationService authenticationService) {
        super(groupRepository, userGroupRepository, authenticationService);
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicDto> getTopicsByGroup(UUID groupId) {
        validateGroupAccess(groupId);
        return topicRepository.findByGroupGroupId(groupId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TopicDto> getOpenTopics(Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        List<UUID> userGroupIds = userGroupRepository.findByUserId(currentUser.getUserId())
                .stream()
                .map(ug -> ug.getGroup().getGroupId())
                .collect(Collectors.toList());

        return topicRepository.findByGroupGroupIdInAndClosedAtIsNull(userGroupIds, pageable)
                .map(this::convertToDto);
    }

    @Transactional
    public TopicDto createTopic(UUID groupId, String title, Set<String> thesisTypes,
                               String problemStatement, String requirements,
                               String goals, String references) {
        validateGroupAccess(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Topic topic = new Topic();
        topic.setGroup(group);
        topic.setTitle(title);
        topic.setThesisTypes(thesisTypes);
        topic.setProblemStatement(problemStatement);
        topic.setRequirements(requirements);
        topic.setGoals(goals);
        topic.setReferences(references);
        topic.setCreatedBy(authenticationService.getCurrentUser());

        return convertToDto(topicRepository.save(topic));
    }

    @Transactional
    public TopicDto updateTopic(UUID topicId, String title, Set<String> thesisTypes,
                               String problemStatement, String requirements,
                               String goals, String references) {
        Topic topic = findTopicById(topicId);
        validateGroupAccess(topic.getGroup().getGroupId());

        topic.setTitle(title);
        topic.setThesisTypes(thesisTypes);
        topic.setProblemStatement(problemStatement);
        topic.setRequirements(requirements);
        topic.setGoals(goals);
        topic.setReferences(references);

        return convertToDto(topicRepository.save(topic));
    }

    @Transactional
    public TopicDto closeTopic(UUID topicId, String reason) {
        Topic topic = findTopicById(topicId);
        validateGroupAccess(topic.getGroup().getGroupId());

        topic.setClosedAt(Instant.now());
        return convertToDto(topicRepository.save(topic));
    }

    private Topic findTopicById(UUID topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    private TopicDto convertToDto(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setThesisTypes(topic.getThesisTypes());
        dto.setProblemStatement(topic.getProblemStatement());
        dto.setRequirements(topic.getRequirements());
        dto.setGoals(topic.getGoals());
        dto.setReferences(topic.getReferences());
        dto.setClosedAt(topic.getClosedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        dto.setCreatedAt(topic.getCreatedAt());
        return dto;
    }
}