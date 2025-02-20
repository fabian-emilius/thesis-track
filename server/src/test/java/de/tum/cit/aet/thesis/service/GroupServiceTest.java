package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
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
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private UUID testGroupId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testGroupId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        testGroup = new Group();
        testGroup.setId(testGroupId);
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");
    }

    @Test
    void getGroupById_ExistingGroup_ReturnsGroup() {
        when(groupRepository.findById(testGroupId)).thenReturn(Optional.of(testGroup));

        Group result = groupService.getGroupById(testGroupId);

        assertNotNull(result);
        assertEquals(testGroupId, result.getId());
        assertEquals("Test Group", result.getName());
    }

    @Test
    void getGroupById_NonExistingGroup_ThrowsException() {
        when(groupRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            groupService.getGroupById(UUID.randomUUID())
        );
    }

    @Test
    void createGroup_NewSlug_CreatesGroup() {
        when(groupRepository.existsBySlug("test-group")).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        Group result = groupService.createGroup(testGroup);

        assertNotNull(result);
        assertEquals("test-group", result.getSlug());
    }

    @Test
    void createGroup_ExistingSlug_ThrowsException() {
        when(groupRepository.existsBySlug("test-group")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
            groupService.createGroup(testGroup)
        );
    }

    @Test
    void isUserInGroup_UserExists_ReturnsTrue() {
        when(userGroupRepository.existsByGroupIdAndUserId(testGroupId, testUserId))
            .thenReturn(true);

        assertTrue(groupService.isUserInGroup(testGroupId, testUserId));
    }

    @Test
    void isUserInGroup_UserDoesNotExist_ReturnsFalse() {
        when(userGroupRepository.existsByGroupIdAndUserId(testGroupId, testUserId))
            .thenReturn(false);

        assertFalse(groupService.isUserInGroup(testGroupId, testUserId));
    }
}