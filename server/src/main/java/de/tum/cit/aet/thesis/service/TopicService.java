package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final GroupService groupService;
    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public Page<Topic> getAllTopics(UUID groupId, Pageable pageable) {
        return topicRepository.findByGroupId(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Topic getTopicById(UUID groupId, UUID id) {
        return topicRepository.findByGroupIdAndId(groupId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    @Transactional
    public Topic createTopic(UUID groupId, Topic topic) {
        Group group = groupService.getGroupById(groupId);
        topic.setGroup(group);
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(UUID groupId, UUID id, Topic topicDetails) {
        Topic topic = getTopicById(groupId, id);
        
        topic.setTitle(topicDetails.getTitle());
        topic.setDescription(topicDetails.getDescription());
        
        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(UUID groupId, UUID id) {
        Topic topic = getTopicById(groupId, id);
        topicRepository.delete(topic);
    }

    @Transactional(readOnly = true)
    public boolean isTopicVisible(UUID groupId, UUID topicId) {
        return topicRepository.existsByGroupIdAndId(groupId, topicId);
    }
}