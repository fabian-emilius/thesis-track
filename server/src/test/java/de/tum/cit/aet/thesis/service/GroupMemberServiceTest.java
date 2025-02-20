package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.mock.EntityMockFactory;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupMemberServiceTest {
    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GroupMemberService groupMemberService;

    private Group testGroup;
    private User testUser;
    private GroupMember testMember;

    @BeforeEach
    void setUp() {
        testGroup = EntityMockFactory.createGroup("Test Group");
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");

        testMember = EntityMockFactory.createGroupMember(testGroup, testUser, GroupRole.ADVISOR);
    }

    @Test
    void getGroupMembers_ShouldReturnMembers() {
        when(groupMemberRepository.findByGroupId(testGroup.getId()))
                .thenReturn(List.of(testMember));

        List<GroupMemberDto> result = groupMemberService.getGroupMembers(testGroup.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.get(0).getRole()).isEqualTo(GroupRole.ADVISOR);
    }

    @Test
    void addGroupMember_WithValidData_ShouldAddMember() {
        when(groupMemberRepository.existsByGroupIdAndUserId(testGroup.getId(), testUser.getId()))
                .thenReturn(false);
        when(groupRepository.findById(testGroup.getId())).thenReturn(Optional.of(testGroup));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(groupMemberRepository.save(any())).thenReturn(testMember);

        GroupMemberDto result = groupMemberService.addGroupMember(
                testGroup.getId(), testUser.getId(), GroupRole.ADVISOR);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getRole()).isEqualTo(GroupRole.ADVISOR);
    }

    @Test
    void addGroupMember_WithExistingMember_ShouldThrowException() {
        when(groupMemberRepository.existsByGroupIdAndUserId(testGroup.getId(), testUser.getId()))
                .thenReturn(true);

        assertThatThrownBy(() -> groupMemberService.addGroupMember(
                testGroup.getId(), testUser.getId(), GroupRole.ADVISOR))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void updateMemberRole_WithValidData_ShouldUpdateRole() {
        when(groupMemberRepository.findByGroupIdAndUserId(testGroup.getId(), testUser.getId()))
                .thenReturn(Optional.of(testMember));
        when(groupMemberRepository.save(any())).thenReturn(testMember);

        GroupMemberDto result = groupMemberService.updateMemberRole(
                testGroup.getId(), testUser.getId(), GroupRole.SUPERVISOR);

        assertThat(result).isNotNull();
        assertThat(result.getRole()).isEqualTo(GroupRole.SUPERVISOR);
    }

    @Test
    void updateMemberRole_WithInvalidMember_ShouldThrowException() {
        when(groupMemberRepository.findByGroupIdAndUserId(any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> groupMemberService.updateMemberRole(
                testGroup.getId(), testUser.getId(), GroupRole.SUPERVISOR))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}