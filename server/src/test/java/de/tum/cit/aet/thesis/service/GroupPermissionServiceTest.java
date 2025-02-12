package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupPermissionServiceTest {
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private GroupPermissionService groupPermissionService;

    private UUID groupId;
    private UUID userId;
    private GroupMember groupMember;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        userId = UUID.randomUUID();

        groupMember = new GroupMember();
        groupMember.setGroupId(groupId);
        groupMember.setUserId(userId);
    }

    @Test
    void validateGroupMember_Success() {
        when(authenticationService.isAdmin()).thenReturn(false);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
            .thenReturn(Optional.of(groupMember));

        assertDoesNotThrow(() -> groupPermissionService.validateGroupMember(groupId));
    }

    @Test
    void validateGroupMember_NotMember_ThrowsException() {
        when(authenticationService.isAdmin()).thenReturn(false);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
            .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, 
            () -> groupPermissionService.validateGroupMember(groupId));
    }

    @Test
    void validateGroupAdmin_Success() {
        groupMember.setRole(GroupRole.GROUP_ADMIN);
        when(authenticationService.isAdmin()).thenReturn(false);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
            .thenReturn(Optional.of(groupMember));

        assertDoesNotThrow(() -> groupPermissionService.validateGroupAdmin(groupId));
    }

    @Test
    void validateGroupAdmin_NotAdmin_ThrowsException() {
        groupMember.setRole(GroupRole.ADVISOR);
        when(authenticationService.isAdmin()).thenReturn(false);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
            .thenReturn(Optional.of(groupMember));

        assertThrows(AccessDeniedException.class, 
            () -> groupPermissionService.validateGroupAdmin(groupId));
    }

    @Test
    void validateSupervisorOrAdmin_Success() {
        groupMember.setRole(GroupRole.SUPERVISOR);
        when(authenticationService.isAdmin()).thenReturn(false);
        when(authenticationService.getCurrentUserId()).thenReturn(userId);
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
            .thenReturn(Optional.of(groupMember));

        assertDoesNotThrow(() -> groupPermissionService.validateSupervisorOrAdmin(groupId));
    }

    @Test
    void systemAdmin_HasAllPermissions() {
        when(authenticationService.isAdmin()).thenReturn(true);

        assertTrue(groupPermissionService.isGroupMember(groupId));
        assertTrue(groupPermissionService.isGroupAdmin(groupId));
        assertTrue(groupPermissionService.isSupervisorOrAdmin(groupId));
    }
}
