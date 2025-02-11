package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupSecurityEvaluatorTest {

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GroupSecurityEvaluator securityEvaluator;

    private UUID userId;
    private UUID groupId;
    private UserGroup userGroup;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        groupId = UUID.randomUUID();

        User user = new User();
        user.setUserId(userId);

        Group group = new Group();
        group.setGroupId(groupId);

        userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);

        when(authentication.getName()).thenReturn(userId.toString());
        when(authentication.isAuthenticated()).thenReturn(true);
    }

    @Test
    void hasGroupAccess_WithValidMembership_ReturnsTrue() {
        userGroup.setRole("MEMBER");
        when(userGroupRepository.findByUserIdAndGroupId(userId, groupId))
                .thenReturn(userGroup);

        assertTrue(securityEvaluator.hasGroupAccess(authentication, groupId));
    }

    @Test
    void hasGroupAccess_WithoutMembership_ReturnsFalse() {
        when(userGroupRepository.findByUserIdAndGroupId(userId, groupId))
                .thenReturn(null);

        assertFalse(securityEvaluator.hasGroupAccess(authentication, groupId));
    }

    @Test
    void hasGroupAccess_WithNullAuthentication_ReturnsFalse() {
        assertFalse(securityEvaluator.hasGroupAccess(null, groupId));
    }

    @Test
    void isGroupAdmin_WithAdminRole_ReturnsTrue() {
        userGroup.setRole("ADMIN");
        when(userGroupRepository.findByUserIdAndGroupId(userId, groupId))
                .thenReturn(userGroup);

        assertTrue(securityEvaluator.isGroupAdmin(authentication, groupId));
    }

    @Test
    void isGroupAdmin_WithMemberRole_ReturnsFalse() {
        userGroup.setRole("MEMBER");
        when(userGroupRepository.findByUserIdAndGroupId(userId, groupId))
                .thenReturn(userGroup);

        assertFalse(securityEvaluator.isGroupAdmin(authentication, groupId));
    }

    @Test
    void isGroupAdmin_WithoutMembership_ReturnsFalse() {
        when(userGroupRepository.findByUserIdAndGroupId(userId, groupId))
                .thenReturn(null);

        assertFalse(securityEvaluator.isGroupAdmin(authentication, groupId));
    }
}