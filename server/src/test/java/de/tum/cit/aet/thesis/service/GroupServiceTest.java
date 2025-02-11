package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TopicService topicService;

    @Mock
    private ThesisService thesisService;

    @InjectMocks
    private GroupService groupService;

    private User currentUser;
    private Group testGroup;
    private UserGroup userGroup;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setUserId(UUID.randomUUID());

        testGroup = new Group();
        testGroup.setGroupId(UUID.randomUUID());
        testGroup.setName("Test Group");
        testGroup.setDescription("Test Description");

        userGroup = new UserGroup();
        userGroup.setUser(currentUser);
        userGroup.setGroup(testGroup);
        userGroup.setRole("ADMIN");

        when(authenticationService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void getAllGroups_ReturnsUserGroups() {
        when(userGroupRepository.findByUserId(currentUser.getUserId()))
                .thenReturn(List.of(userGroup));

        List<GroupDto> groups = groupService.getAllGroups();

        assertFalse(groups.isEmpty());
        assertEquals(testGroup.getName(), groups.get(0).getName());
    }

    @Test
    void getGroup_WithAccess_ReturnsGroup() {
        when(groupRepository.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);

        GroupDto group = groupService.getGroup(testGroup.getGroupId());

        assertNotNull(group);
        assertEquals(testGroup.getName(), group.getName());
    }

    @Test
    void getGroup_WithoutAccess_ThrowsException() {
        when(groupRepository.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(null);

        assertThrows(AccessDeniedException.class, () ->
                groupService.getGroup(testGroup.getGroupId()));
    }

    @Test
    void createGroup_CreatesGroupAndAddsAdmin() {
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        when(userGroupRepository.save(any(UserGroup.class))).thenReturn(userGroup);

        GroupDto created = groupService.createGroup("Test Group", "Test Description");

        assertNotNull(created);
        assertEquals(testGroup.getName(), created.getName());
    }

    @Test
    void updateGroup_AsAdmin_UpdatesGroup() {
        when(groupRepository.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        GroupDto updated = groupService.updateGroup(
                testGroup.getGroupId(), "Updated Name", "Updated Description");

        assertNotNull(updated);
        assertEquals("Updated Name", testGroup.getName());
    }

    @Test
    void updateGroup_AsNonAdmin_ThrowsException() {
        userGroup.setRole("MEMBER");
        when(groupRepository.findById(testGroup.getGroupId()))
                .thenReturn(Optional.of(testGroup));
        when(userGroupRepository.findByUserIdAndGroupId(currentUser.getUserId(), testGroup.getGroupId()))
                .thenReturn(userGroup);

        assertThrows(AccessDeniedException.class, () ->
                groupService.updateGroup(testGroup.getGroupId(), "Updated Name", "Updated Description"));
    }
}