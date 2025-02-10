package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.PublishedThesisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PublishedThesisServiceTest {
    @Mock
    private PublishedThesisRepository publishedThesisRepository;

    private PublishedThesisService publishedThesisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        publishedThesisService = new PublishedThesisService(publishedThesisRepository);
    }

    @Test
    void getVisibleTheses_ShouldReturnVisibleTheses() {
        UUID groupId = UUID.randomUUID();
        List<PublishedThesis> theses = Arrays.asList(new PublishedThesis(), new PublishedThesis());
        when(publishedThesisRepository.findVisibleTheses(groupId)).thenReturn(theses);

        List<PublishedThesis> result = publishedThesisService.getVisibleTheses(groupId);

        assertEquals(2, result.size());
    }

    @Test
    void publishThesis_ShouldReturnPublishedThesis() {
        Thesis thesis = new Thesis();
        User publisher = new User();
        Set<UUID> visibilityGroups = new HashSet<>();
        PublishedThesis publishedThesis = new PublishedThesis();

        when(publishedThesisRepository.save(any(PublishedThesis.class))).thenReturn(publishedThesis);

        PublishedThesis result = publishedThesisService.publishThesis(thesis, publisher, visibilityGroups);

        assertNotNull(result);
    }

    @Test
    void getPublishedThesisById_WithValidId_ShouldReturnPublishedThesis() {
        UUID id = UUID.randomUUID();
        PublishedThesis publishedThesis = new PublishedThesis();
        when(publishedThesisRepository.findById(id)).thenReturn(Optional.of(publishedThesis));

        PublishedThesis result = publishedThesisService.getPublishedThesisById(id);

        assertNotNull(result);
    }

    @Test
    void getPublishedThesisById_WithInvalidId_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(publishedThesisRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publishedThesisService.getPublishedThesisById(id));
    }
}
