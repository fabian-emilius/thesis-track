package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicGroupService {
    private final TopicRepository topicRepository;
    private final GroupService groupService;

    public List<Topic> getTopicsByGroup(UUID groupId) {
        Group group = groupService.getGroupById(groupId);
        return topicRepository.findByGroup(group);
    }

    public void assignTopicToGroup(UUID topicId, UUID groupId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        Group group = groupService.getGroupById(groupId);
        
        topic.setGroup(group);
        topicRepository.save(topic);
    }

    public void removeTopicFromGroup(UUID topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        
        topic.setGroup(null);
        topicRepository.save(topic);
    }
}