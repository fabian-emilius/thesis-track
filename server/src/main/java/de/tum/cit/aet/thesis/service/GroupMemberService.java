package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.key.GroupMemberId;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupMemberService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<GroupMemberDto> getGroupMembers(UUID groupId) {
        return groupMemberRepository.findByGroupId(groupId).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public GroupMemberDto addGroupMember(UUID groupId, UUID userId, GroupRole role) {
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ResourceAlreadyExistsException("User is already a member of this group");
        }

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(groupId, userId));
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);

        member = groupMemberRepository.save(member);
        return mapToDto(member);
    }

    @Transactional
    public void removeGroupMember(UUID groupId, UUID userId) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group member not found"));
        groupMemberRepository.delete(member);
    }

    @Transactional
    public GroupMemberDto updateMemberRole(UUID groupId, UUID userId, GroupRole newRole) {
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group member not found"));
        
        member.setRole(newRole);
        member = groupMemberRepository.save(member);
        return mapToDto(member);
    }

    private GroupMemberDto mapToDto(GroupMember member) {
        GroupMemberDto dto = new GroupMemberDto();
        dto.setUserId(member.getUser().getId());
        dto.setUsername(member.getUser().getUsername());
        dto.setFullName(member.getUser().getFullName());
        dto.setRole(member.getRole());
        return dto;
    }
}