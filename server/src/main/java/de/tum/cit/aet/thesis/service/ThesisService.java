package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ThesisService {
    private final ThesisRepository thesisRepository;
    private final TopicService topicService;
    private final GroupService groupService;

    public ThesisService(ThesisRepository thesisRepository, 
                        TopicService topicService,
                        GroupService groupService) {
        this.thesisRepository = thesisRepository;
        this.topicService = topicService;
        this.groupService = groupService;
    }

    public Page<Thesis> getThesesByGroup(UUID groupId, Pageable pageable) {
        Group group = groupService.getGroupById(groupId);
        return thesisRepository.findByTopicGroup(group, pageable);
    }

    public Thesis getThesisById(UUID id) {
        return thesisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found"));
    }

    @Transactional
    public Thesis createThesis(Thesis thesis, UUID topicId) {
        Topic topic = topicService.getTopicById(topicId);
        thesis.setTopic(topic);
        return thesisRepository.save(thesis);
    }

    @Transactional
    public Thesis updateThesis(UUID id, Thesis updatedThesis) {
        Thesis thesis = getThesisById(id);
        // Update basic thesis information
        thesis.setTitle(updatedThesis.getTitle());
        thesis.setDescription(updatedThesis.getDescription());
        // Maintain group context from topic
        return thesisRepository.save(thesis);
    }

    public boolean isUserAuthorizedForThesis(UUID thesisId, UUID userId) {
        Thesis thesis = getThesisById(thesisId);
        // Check if user belongs to the thesis's group
        return thesis.getTopic().getGroup() == null || 
               thesis.getTopic().getGroup().getId().equals(userId);
    }
}
