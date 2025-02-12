package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
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
    private GroupPermissionService groupPermissionService;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private GroupService groupService;

    private Group testGroup;
    private User testUser;
    private GroupDto testGroupDto;

    @BeforeEach
    void setUp() {
        testGroup = new Group();
        testGroup.setId(UUID.randomUUID());
        testGroup.setName("Test Group");
        testGroup.setSlug("test-group");

        testUser = new User();
        testUser.setId(UUID.randomUUID());

        testGroupDto = new GroupDto();
        testGroupDto.setName("Test Group");
        testGroupDto.setSlug("test-group");
    }

    @Test
    void createGroup_Success() {
        when(groupRepository.existsBySlug(anyString())).thenReturn(false);
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        when(groupMemberRepository.save(any(GroupMember.class))).thenReturn(new GroupMember());

        Group result = groupService.createGroup(testGroupDto, testUser);

        assertNotNull(result);
        assertEquals(testGroup.getName(), result.getName());
        assertEquals(testGroup.getSlug(), result.getSlug());

        verify(groupRepository).save(any(Group.class));
        verify(groupMemberRepository).save(any(GroupMember.class));
    }

    @Test
    void createGroup_DuplicateSlug_ThrowsException() {
        when(groupRepository.existsBySlug(anyString())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, 
            () -> groupService.createGroup(testGroupDto, testUser));

        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void getGroupById_Success() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(testGroup));

        Group result = groupService.getGroupById(testGroup.getId());

        assertNotNull(result);
        assertEquals(testGroup.getId(), result.getId());
    }

    @Test
    void getGroupById_NotFound_ThrowsException() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, 
            () -> groupService.getGroupById(UUID.randomUUID()));
    }

    @Test
    void updateGroup_Success() {
        when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);
        doNothing().when(groupPermissionService).validateGroupAdmin(any(UUID.class));

        Group result = groupService.updateGroup(testGroup.getId(), testGroupDto);

        assertNotNull(result);
        assertEquals(testGroupDto.getName(), result.getName());
        verify(groupRepository).save(any(Group.class));
    }

    @Test
    void getGroupMembers_Success() {
        List<GroupMember> members = List.of(new GroupMember());
        when(groupMemberRepository.findByGroupId(any(UUID.class))).thenReturn(members);
        doNothing().when(groupPermissionService).validateGroupMember(any(UUID.class));

        List<GroupMember> result = groupService.getGroupMembers(testGroup.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(groupMemberRepository).findByGroupId(testGroup.getId());
    }
}
