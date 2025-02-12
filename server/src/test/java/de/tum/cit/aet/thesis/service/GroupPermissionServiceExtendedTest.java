package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.mock.BaseIntegrationTest;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroupPermissionServiceExtendedTest extends BaseIntegrationTest {

    @Autowired
    private GroupPermissionService groupPermissionService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    private Group testGroup;
    private User testUser;
    private GroupMember testMembership;

    @BeforeEach
    void setUp() {
        groupRepository.deleteAll();
        groupMemberRepository.deleteAll();

        testGroup = new Group();
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");
        testGroup = groupRepository.save(testGroup);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser = userRepository.save(testUser);

        testMembership = new GroupMember();
        testMembership.setGroupId(testGroup.getId());
        testMembership.setUserId(testUser.getId());
        testMembership.setRole(GroupRole.ADVISOR);
        testMembership = groupMemberRepository.save(testMembership);
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGroupMemberAccess() {
        assertTrue(groupPermissionService.isGroupMember(testGroup.getId()));
        assertFalse(groupPermissionService.isGroupAdmin(testGroup.getId()));
        assertFalse(groupPermissionService.isSupervisorOrAdmin(testGroup.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testSupervisorAccess() {
        testMembership.setRole(GroupRole.SUPERVISOR);
        groupMemberRepository.save(testMembership);

        assertTrue(groupPermissionService.isGroupMember(testGroup.getId()));
        assertFalse(groupPermissionService.isGroupAdmin(testGroup.getId()));
        assertTrue(groupPermissionService.isSupervisorOrAdmin(testGroup.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testGroupAdminAccess() {
        testMembership.setRole(GroupRole.GROUP_ADMIN);
        groupMemberRepository.save(testMembership);

        assertTrue(groupPermissionService.isGroupMember(testGroup.getId()));
        assertTrue(groupPermissionService.isGroupAdmin(testGroup.getId()));
        assertTrue(groupPermissionService.isSupervisorOrAdmin(testGroup.getId()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testNonMemberAccess() {
        UUID otherGroupId = UUID.randomUUID();

        assertFalse(groupPermissionService.isGroupMember(otherGroupId));
        assertFalse(groupPermissionService.isGroupAdmin(otherGroupId));
        assertFalse(groupPermissionService.isSupervisorOrAdmin(otherGroupId));

        assertThrows(AccessDeniedException.class, 
            () -> groupPermissionService.validateGroupMember(otherGroupId));
        assertThrows(AccessDeniedException.class, 
            () -> groupPermissionService.validateGroupAdmin(otherGroupId));
        assertThrows(AccessDeniedException.class, 
            () -> groupPermissionService.validateSupervisorOrAdmin(otherGroupId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSystemAdminAccess() {
        UUID anyGroupId = UUID.randomUUID();

        assertTrue(groupPermissionService.isGroupMember(anyGroupId));
        assertTrue(groupPermissionService.isGroupAdmin(anyGroupId));
        assertTrue(groupPermissionService.isSupervisorOrAdmin(anyGroupId));

        assertDoesNotThrow(() -> groupPermissionService.validateGroupMember(anyGroupId));
        assertDoesNotThrow(() -> groupPermissionService.validateGroupAdmin(anyGroupId));
        assertDoesNotThrow(() -> groupPermissionService.validateSupervisorOrAdmin(anyGroupId));
    }
}
