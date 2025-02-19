package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.GroupMemberId;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.service.api.IGroupMemberService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing research group memberships.
 * Handles operations related to adding, removing, and querying group members.
 */
@Service
@Validated
@RequiredArgsConstructor
public class GroupMemberService implements IGroupMemberService {
    private final GroupMemberRepository memberRepository;

    /**
     * Retrieves all members of a specific research group.
     *
     * @param groupId The UUID of the research group
     * @return List of group members
     * @throws ValidationException if groupId is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupMember> getGroupMembers(@NotNull UUID groupId) {
        if (groupId == null) {
            throw new ResourceInvalidParametersException("Group ID cannot be null");
        }
        return memberRepository.findAllByGroupId(groupId);
    }

    /**
     * Retrieves all group memberships for a specific user.
     *
     * @param userId The UUID of the user
     * @return List of group memberships
     * @throws ValidationException if userId is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<GroupMember> getUserMemberships(@NotNull UUID userId) {
        if (userId == null) {
            throw new ResourceInvalidParametersException("User ID cannot be null");
        }
        return memberRepository.findAllByUserId(userId);
    }

    /**
     * Adds a new member to a research group with a specific role.
     *
     * @param group The research group
     * @param user The user to add
     * @param role The role to assign
     * @return The created group membership
     * @throws ValidationException if any parameter is null or if membership already exists
     */
    @Override
    @Transactional
    public GroupMember addMember(@NotNull ResearchGroup group, @NotNull User user, @NotNull GroupRole role) {
        if (group == null || user == null || role == null) {
            throw new ResourceInvalidParametersException("Group, user and role must not be null");
        }
        
        if (isMember(group.getId(), user.getId())) {
            throw new ResourceInvalidParametersException("User is already a member of this group");
        }

        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(group.getId(), user.getId()));
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(OffsetDateTime.now());

        return memberRepository.save(member);
    }

    /**
     * Adds a new group admin to a research group.
     *
     * @param group The research group
     * @param user The user to add as admin
     * @return The created group membership
     * @throws ValidationException if group or user is null
     */
    @Override
    @Transactional
    public GroupMember addGroupAdmin(@NotNull ResearchGroup group, @NotNull User user) {
        if (group == null || user == null) {
            throw new ResourceInvalidParametersException("Group and user must not be null");
        }
        return addMember(group, user, GroupRole.GROUP_ADMIN);
    }

    /**
     * Removes a member from a research group.
     *
     * @param groupId The UUID of the research group
     * @param userId The UUID of the user to remove
     * @throws ValidationException if groupId or userId is null
     */
    @Override
    @Transactional
    public void removeMember(@NotNull UUID groupId, @NotNull UUID userId) {
        if (groupId == null || userId == null) {
            throw new ResourceInvalidParametersException("Group ID and User ID must not be null");
        }
        memberRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    /**
     * Checks if a user has a specific role in a research group.
     *
     * @param groupId The UUID of the research group
     * @param userId The UUID of the user
     * @param role The role to check
     * @return true if the user has the specified role, false otherwise
     * @throws ValidationException if any parameter is null
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(@NotNull UUID groupId, @NotNull UUID userId, @NotNull GroupRole role) {
        if (groupId == null || userId == null || role == null) {
            throw new ResourceInvalidParametersException("Group ID, User ID and Role must not be null");
        }
        return memberRepository.existsByGroupIdAndUserIdAndRole(groupId, userId, role);
    }

    /**
     * Checks if a user is a member of a research group.
     *
     * @param groupId The UUID of the research group
     * @param userId The UUID of the user
     * @return true if the user is a member, false otherwise
     * @throws ValidationException if groupId or userId is null
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isMember(@NotNull UUID groupId, @NotNull UUID userId) {
        if (groupId == null || userId == null) {
            throw new ValidationException("Group ID and User ID must not be null");
        }
        return memberRepository.findAllByUserId(userId).stream()
                .anyMatch(member -> member.getGroup().getId().equals(groupId));
    }
}