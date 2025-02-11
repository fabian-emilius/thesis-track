package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.ThesisDto;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ThesisService extends BaseGroupService {
    private final ThesisRepository thesisRepository;
    private final TopicRepository topicRepository;

    public ThesisService(AuthenticationService authenticationService,
                        UserGroupRepository userGroupRepository,
                        GroupRepository groupRepository,
                        ThesisRepository thesisRepository,
                        TopicRepository topicRepository) {
        super(authenticationService, userGroupRepository, groupRepository);
        this.thesisRepository = thesisRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public List<ThesisDto> getThesesByGroup(UUID groupId) {
        validateGroupAccess(groupId);
        return thesisRepository.findByTopicGroupGroupId(groupId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ThesisDto createThesis(UUID topicId, String title, String description) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        
        validateGroupAccess(topic.getGroup().getGroupId());

        Thesis thesis = new Thesis();
        thesis.setTopic(topic);
        thesis.setTitle(title);
        // Set other thesis properties

        return convertToDto(thesisRepository.save(thesis));
    }

    @Transactional
    public ThesisDto updateThesis(UUID thesisId, String title, String description) {
        Thesis thesis = findThesisById(thesisId);
        validateGroupAccess(thesis.getTopic().getGroup().getGroupId());

        thesis.setTitle(title);
        // Update other thesis properties

        return convertToDto(thesisRepository.save(thesis));
    }

    private Thesis findThesisById(UUID thesisId) {
        return thesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found"));
    }

    

    private ThesisDto convertToDto(Thesis thesis) {
        ThesisDto dto = new ThesisDto();
        dto.setId(thesis.getId());
        dto.setTitle(thesis.getTitle());
        // Set other DTO properties
        return dto;
    }
}