package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupSecurityServiceTest {

    @Mock
    private GroupService groupService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private GroupSecurityService groupSecurityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void hasGroupAccess_WithAdminRole_ShouldReturnTrue() {
        // Arrange
        when(authentication.getAuthorities()).thenReturn(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // Act
        boolean result = groupSecurityService.hasGroupAccess(UUID.randomUUID());

        // Assert
        assertTrue(result);
    }

    @Test
    void canAccessTopic_WithNullGroup_ShouldReturnTrue() {
        // Arrange
        Topic topic = new Topic();
        topic.setGroup(null);

        // Act
        boolean result = groupSecurityService.canAccessTopic(topic);

        // Assert
        assertTrue(result);
    }

    @Test
    void canAccessPublishedThesis_WithEmptyVisibilityGroups_ShouldReturnTrue() {
        // Arrange
        PublishedThesis thesis = new PublishedThesis();
        thesis.setVisibilityGroups(new HashSet<>());

        // Act
        boolean result = groupSecurityService.canAccessPublishedThesis(thesis);

        // Assert
        assertTrue(result);
    }

    @Test
    void canAccessPublishedThesis_WithAdminRole_ShouldReturnTrue() {
        // Arrange
        PublishedThesis thesis = new PublishedThesis();
        Set<UUID> visibilityGroups = new HashSet<>();
        visibilityGroups.add(UUID.randomUUID());
        thesis.setVisibilityGroups(visibilityGroups);

        when(authentication.getAuthorities()).thenReturn(
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // Act
        boolean result = groupSecurityService.canAccessPublishedThesis(thesis);

        // Assert
        assertTrue(result);
    }
}