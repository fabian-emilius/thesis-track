package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private User testUser;
    private GroupMember testGroupMember;

    @BeforeEach
    void setUp() {
        testGroup = new Group();
        testGroup.setId(UUID.randomUUID());
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");

        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testGroupMember = new GroupMember();
        testGroupMember.setGroup(testGroup);
        testGroupMember.setUser(testUser);
        testGroupMember.setRole("supervisor");
    }

    @Test
    void createGroup_Success() {
        when(groupRepository.existsBySlug(anyString())).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        GroupDto result = groupService.createGroup(
            "Test Group",
            "test-group",
            "Test Description",
            "logo.png",
            "https://example.com"
        );

        assertNotNull(result);
        assertEquals(testGroup.getName(), result.getName());
        assertEquals(testGroup.getSlug(), result.getSlug());
    }

    @Test
    void createGroup_DuplicateSlug() {
        when(groupRepository.existsBySlug(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () ->
            groupService.createGroup(
                "Test Group",
                "test-group",
                "Test Description",
                "logo.png",
                "https://example.com"
            )
        );
    }

    @Test
    void getGroupById_Success() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(testGroup));

        GroupDto result = groupService.getGroupById(testGroup.getId());

        assertNotNull(result);
        assertEquals(testGroup.getId(), result.getId());
    }

    @Test
    void getGroupById_NotFound() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            groupService.getGroupById(UUID.randomUUID())
        );
    }

    @Test
    void addGroupMember_Success() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(testGroupMember);

        GroupMemberDto result = groupService.addGroupMember(
            testGroup.getId(),
            testUser.getId(),
            "supervisor"
        );

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals("supervisor", result.getRole());
    }

    @Test
    void getGroupMembers_Success() {
        when(groupMemberRepository.findByGroupId(any(UUID.class)))
            .thenReturn(List.of(testGroupMember));

        List<GroupMemberDto> results = groupService.getGroupMembers(testGroup.getId());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testUser.getId(), results.get(0).getUserId());
    }
}
