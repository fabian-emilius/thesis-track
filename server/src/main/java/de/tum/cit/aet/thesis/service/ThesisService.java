package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Application;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.ThesisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThesisService {
    private final ThesisRepository thesisRepository;
    private final ApplicationService applicationService;
    private final GroupService groupService;

    @Transactional(readOnly = true)
    public Page<Thesis> getAllTheses(UUID groupId, Pageable pageable) {
        return thesisRepository.findByGroupId(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public Thesis getThesisById(UUID groupId, UUID id) {
        return thesisRepository.findByGroupIdAndId(groupId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found"));
    }

    @Transactional
    public Thesis createThesis(UUID groupId, UUID applicationId, Thesis thesis) {
        Group group = groupService.getGroupById(groupId);
        Application application = applicationService.getApplicationById(groupId, applicationId);

        thesis.setGroup(group);
        thesis.setApplication(application);

        return thesisRepository.save(thesis);
    }

    @Transactional
    public Thesis updateThesis(UUID groupId, UUID id, Thesis thesisDetails) {
        Thesis thesis = getThesisById(groupId, id);
        
        // Update thesis fields as needed
        // Note: group and application should not be changed
        
        return thesisRepository.save(thesis);
    }

    @Transactional(readOnly = true)
    public boolean isThesisVisible(UUID groupId, UUID thesisId) {
        return thesisRepository.existsByGroupIdAndId(groupId, thesisId);
    }
}