package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.PublishedThesisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PublishedThesisService {
    private final PublishedThesisRepository publishedThesisRepository;

    @Transactional(readOnly = true)
    public Page<PublishedThesis> getVisibleTheses(UUID groupId, Pageable pageable) {
        return publishedThesisRepository.findVisibleTheses(groupId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean isThesisVisibleToGroup(UUID thesisId, UUID groupId) {
        return publishedThesisRepository.isThesisVisibleToGroup(thesisId, groupId);
    }

    @Transactional
    public PublishedThesis publishThesis(Thesis thesis, Set<UUID> visibilityGroups) {
        PublishedThesis publishedThesis = new PublishedThesis();
        publishedThesis.setThesis(thesis);
        publishedThesis.setVisibilityGroups(visibilityGroups);
        publishedThesis.setCreatedAt(LocalDateTime.now());
        publishedThesis.setUpdatedAt(LocalDateTime.now());
        
        return publishedThesisRepository.save(publishedThesis);
    }

    @Transactional
    public PublishedThesis updateVisibilityGroups(UUID thesisId, Set<UUID> visibilityGroups) {
        PublishedThesis publishedThesis = publishedThesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Published thesis not found"));

        publishedThesis.setVisibilityGroups(visibilityGroups);
        publishedThesis.setUpdatedAt(LocalDateTime.now());

        return publishedThesisRepository.save(publishedThesis);
    }

    @Transactional
    public void unpublishThesis(UUID thesisId) {
        publishedThesisRepository.deleteById(thesisId);
    }
}
