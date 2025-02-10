package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TopicService {
    private final TopicRepository topicRepository;
    private final GroupService groupService;

    public TopicService(TopicRepository topicRepository, GroupService groupService) {
        this.topicRepository = topicRepository;
        this.groupService = groupService;
    }

    public Page<Topic> getTopicsByGroup(UUID groupId, Pageable pageable) {
        Group group = groupService.getGroupById(groupId);
        return topicRepository.findByGroup(group, pageable);
    }

    public Topic getTopicById(UUID id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    @Transactional
    public Topic createTopic(Topic topic, UUID groupId) {
        if (groupId != null) {
            Group group = groupService.getGroupById(groupId);
            topic.setGroup(group);
        }
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(UUID id, Topic updatedTopic) {
        Topic topic = getTopicById(id);
        topic.setTitle(updatedTopic.getTitle());
        topic.setThesisTypes(updatedTopic.getThesisTypes());
        topic.setProblemStatement(updatedTopic.getProblemStatement());
        topic.setRequirements(updatedTopic.getRequirements());
        topic.setGoals(updatedTopic.getGoals());
        topic.setReferences(updatedTopic.getReferences());
        
        if (updatedTopic.getGroup() != null) {
            Group group = groupService.getGroupById(updatedTopic.getGroup().getId());
            topic.setGroup(group);
        }
        
        return topicRepository.save(topic);
    }
}
