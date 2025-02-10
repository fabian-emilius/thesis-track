package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupAccessServiceTest {
    @Mock
    private UserGroupRepository userGroupRepository;

    @InjectMocks
    private GroupAccessService groupAccessService;

    private User adminUser;
    private User supervisorUser;
    private User regularUser;
    private UserGroup group;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminUser = new User();
        adminUser.setGroups(Set.of("admin"));

        supervisorUser = new User();
        supervisorUser.setGroups(Set.of("supervisor"));

        regularUser = new User();
        regularUser.setGroups(Set.of("student"));

        group = new UserGroup();
        group.setId(UUID.randomUUID());
    }

    @Test
    void adminHasAccessToAllGroups() {
        assertTrue(groupAccessService.hasGroupAccess(adminUser, group));
    }

    @Test
    void supervisorHasAccessToGroups() {
        assertTrue(groupAccessService.hasGroupAccess(supervisorUser, group));
    }

    @Test
    void regularUserHasNoGroupAccess() {
        assertFalse(groupAccessService.hasGroupAccess(regularUser, group));
    }

    @Test
    void validateGroupAccessThrowsForUnauthorizedUser() {
        assertThrows(AccessDeniedException.class, () ->
            groupAccessService.validateGroupAccess(regularUser, group)
        );
    }

    @Test
    void validateGroupAccessPassesForAuthorizedUser() {
        assertDoesNotThrow(() ->
            groupAccessService.validateGroupAccess(adminUser, group)
        );
    }
}
