package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.UnauthorizedAccessException;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private GroupAccessService groupAccessService;

    @InjectMocks
    private UserService userService;

    private UserGroup testGroup;
    private UUID groupId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        groupId = UUID.randomUUID();
        testGroup = new UserGroup();
        testGroup.setId(groupId);
        testGroup.setName("Test Group");
        testGroup.setDescription("Test Description");

        when(groupAccessService.canCreateGroup()).thenReturn(true);
        when(groupAccessService.canModifyGroup(any())).thenReturn(true);
        when(groupAccessService.canDeleteGroup(any())).thenReturn(true);
        when(groupAccessService.hasAccess(any(UserGroup.class))).thenReturn(true);
    }

    @Test
    void getAllGroupsReturnsPagedResults() {
        Page<UserGroup> groupPage = new PageImpl<>(List.of(testGroup));
        when(userGroupRepository.findAllByUserHasAccess(any(), any(Pageable.class))).thenReturn(groupPage);

        Page<UserGroup> result = userService.getAllGroups(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testGroup.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getGroupByIdReturnsGroup() {
        when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        UserGroup result = userService.getGroupById(groupId);

        assertNotNull(result);
        assertEquals(testGroup.getName(), result.getName());
    }

    @Test
    void getGroupByIdThrowsWhenNotFound() {
        when(userGroupRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userService.getGroupById(UUID.randomUUID())
        );
    }

    @Test
    void createGroupSucceeds() {
        when(userGroupRepository.save(any())).thenReturn(testGroup);

        UserGroup result = userService.createGroup("Test Group", "Test Description");

        assertNotNull(result);
        assertEquals(testGroup.getName(), result.getName());
        verify(userGroupRepository).save(any());
    }

    @Test
    void createGroupThrowsWhenUnauthorized() {
        when(groupAccessService.canCreateGroup()).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () ->
            userService.createGroup("Test Group", "Test Description")
        );
    }

    @Test
    void updateGroupSucceeds() {
        when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(userGroupRepository.save(any())).thenReturn(testGroup);

        UserGroup result = userService.updateGroup(groupId, "Updated Name", "Updated Description");

        assertNotNull(result);
        verify(userGroupRepository).save(any());
    }

    @Test
    void deleteGroupSucceeds() {
        when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        assertDoesNotThrow(() -> userService.deleteGroup(groupId));

        verify(userGroupRepository).delete(testGroup);
    }

    @Test
    void deleteGroupThrowsWhenUnauthorized() {
        when(userGroupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));
        when(groupAccessService.canDeleteGroup(any())).thenReturn(false);

        assertThrows(UnauthorizedAccessException.class, () ->
            userService.deleteGroup(groupId)
        );
    }
}
