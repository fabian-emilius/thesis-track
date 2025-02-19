package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupPermissionEvaluatorTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserGroupRepository userGroupRepository;

    @InjectMocks
    private GroupPermissionEvaluator permissionEvaluator;

    private UUID userId;
    private UUID groupId;
    private UserGroup userGroup;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        
        userGroup = new UserGroup();
        userGroup.setRole("admin");
    }

    @Test
    void isGroupAdmin_WithAdminRole_ShouldReturnTrue() {
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.of(userGroup));

        assertTrue(permissionEvaluator.isGroupAdmin(groupId));
    }

    @Test
    void isGroupAdmin_WithNonAdminRole_ShouldReturnFalse() {
        userGroup.setRole("member");
        
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.of(userGroup));

        assertFalse(permissionEvaluator.isGroupAdmin(groupId));
    }

    @Test
    void isGroupMember_WithValidMembership_ShouldReturnTrue() {
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.of(userGroup));

        assertTrue(permissionEvaluator.isGroupMember(groupId));
    }

    @Test
    void isGroupMember_WithNoMembership_ShouldReturnFalse() {
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.empty());

        assertFalse(permissionEvaluator.isGroupMember(groupId));
    }

    @Test
    void hasGroupRole_WithMatchingRole_ShouldReturnTrue() {
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.of(userGroup));

        assertTrue(permissionEvaluator.hasGroupRole(groupId, "admin"));
    }

    @Test
    void hasGroupRole_WithNonMatchingRole_ShouldReturnFalse() {
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(userGroupRepository.findById(new UserGroup.UserGroupId(userId, groupId)))
                .thenReturn(Optional.of(userGroup));

        assertFalse(permissionEvaluator.hasGroupRole(groupId, "supervisor"));
    }
}