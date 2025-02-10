package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Thesis;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.PublishedThesisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublishedThesisServiceTest {
    @Mock
    private PublishedThesisRepository publishedThesisRepository;

    @InjectMocks
    private PublishedThesisService publishedThesisService;

    private PublishedThesis testPublishedThesis;
    private Thesis testThesis;
    private UUID thesisId;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        thesisId = UUID.randomUUID();
        groupId = UUID.randomUUID();

        testThesis = new Thesis();
        testThesis.setId(thesisId);

        testPublishedThesis = new PublishedThesis();
        testPublishedThesis.setId(thesisId);
        testPublishedThesis.setThesis(testThesis);
        testPublishedThesis.setVisibilityGroups(Set.of(groupId));
        testPublishedThesis.setCreatedAt(LocalDateTime.now());
        testPublishedThesis.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getVisibleThesesReturnsPagedResults() {
        Page<PublishedThesis> thesisPage = new PageImpl<>(List.of(testPublishedThesis));
        when(publishedThesisRepository.findVisibleTheses(any(), any(Pageable.class))).thenReturn(thesisPage);

        Page<PublishedThesis> result = publishedThesisService.getVisibleTheses(groupId, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testPublishedThesis.getId(), result.getContent().get(0).getId());
    }

    @Test
    void isThesisVisibleToGroupReturnsTrue() {
        when(publishedThesisRepository.isThesisVisibleToGroup(thesisId, groupId)).thenReturn(true);

        assertTrue(publishedThesisService.isThesisVisibleToGroup(thesisId, groupId));
    }

    @Test
    void publishThesisCreatesNewPublishedThesis() {
        when(publishedThesisRepository.save(any())).thenReturn(testPublishedThesis);

        PublishedThesis result = publishedThesisService.publishThesis(testThesis, Set.of(groupId));

        assertNotNull(result);
        assertEquals(testThesis, result.getThesis());
        assertTrue(result.getVisibilityGroups().contains(groupId));
        verify(publishedThesisRepository).save(any());
    }

    @Test
    void updateVisibilityGroupsUpdatesExistingThesis() {
        when(publishedThesisRepository.findById(thesisId)).thenReturn(Optional.of(testPublishedThesis));
        when(publishedThesisRepository.save(any())).thenReturn(testPublishedThesis);

        UUID newGroupId = UUID.randomUUID();
        PublishedThesis result = publishedThesisService.updateVisibilityGroups(thesisId, Set.of(newGroupId));

        assertNotNull(result);
        assertTrue(result.getVisibilityGroups().contains(newGroupId));
        verify(publishedThesisRepository).save(any());
    }

    @Test
    void updateVisibilityGroupsThrowsWhenNotFound() {
        when(publishedThesisRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            publishedThesisService.updateVisibilityGroups(UUID.randomUUID(), Set.of(groupId))
        );
    }

    @Test
    void unpublishThesisDeletesPublishedThesis() {
        doNothing().when(publishedThesisRepository).deleteById(any());

        assertDoesNotThrow(() -> publishedThesisService.unpublishThesis(thesisId));

        verify(publishedThesisRepository).deleteById(thesisId);
    }
}
