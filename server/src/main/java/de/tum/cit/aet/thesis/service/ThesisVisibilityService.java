package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.repository.PublishedThesisRepository;
import de.tum.cit.aet.thesis.security.GroupSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThesisVisibilityService {
    private final PublishedThesisRepository publishedThesisRepository;
    private final GroupSecurityService groupSecurityService;

    public List<PublishedThesis> getVisibleTheses() {
        List<PublishedThesis> allTheses = publishedThesisRepository.findAll();
        return allTheses.stream()
                .filter(groupSecurityService::canAccessPublishedThesis)
                .toList();
    }

    public void updateThesisVisibility(UUID thesisId, Set<UUID> visibilityGroups) {
        PublishedThesis thesis = publishedThesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Published thesis not found"));
        
        thesis.setVisibilityGroups(visibilityGroups);
        publishedThesisRepository.save(thesis);
    }
}