package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.PublishedThesisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PublishedThesisService {
    private final PublishedThesisRepository publishedThesisRepository;

    public PublishedThesisService(PublishedThesisRepository publishedThesisRepository) {
        this.publishedThesisRepository = publishedThesisRepository;
    }

    public List<PublishedThesis> getVisibleTheses(UUID groupId) {
        return publishedThesisRepository.findVisibleTheses(groupId);
    }

    @Transactional
    public PublishedThesis publishThesis(Thesis thesis, User publisher, Set<UUID> visibilityGroups) {
        PublishedThesis publishedThesis = new PublishedThesis();
        publishedThesis.setThesis(thesis);
        publishedThesis.setCreatedBy(publisher);
        publishedThesis.setVisibilityGroups(visibilityGroups);
        return publishedThesisRepository.save(publishedThesis);
    }

    public PublishedThesis getPublishedThesisById(UUID id) {
        return publishedThesisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Published thesis not found"));
    }

    @Transactional
    public void updateVisibility(UUID id, Set<UUID> visibilityGroups) {
        PublishedThesis publishedThesis = getPublishedThesisById(id);
        publishedThesis.setVisibilityGroups(visibilityGroups);
        publishedThesisRepository.save(publishedThesis);
    }
}
