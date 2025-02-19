package de.tum.cit.aet.thesis.service.interfaces;

import de.tum.cit.aet.thesis.constants.enums.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.NotFoundException;
import de.tum.cit.aet.thesis.exception.ValidationException;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing group memberships.
 */
public interface IGroupMemberService {
    /**
     * Retrieves all members of a group.
     *
     * @param groupId The ID of the group
     * @return List of group members
     */
    List<GroupMember> getGroupMembers(UUID groupId);

    /**
     * Retrieves all group memberships for a user.
     *
     * @param userId The ID of the user
     * @return List of group memberships
     */
    List<GroupMember> getUserMemberships(UUID userId);

    /**
     * Adds a new member to a group.
     *
     * @param group The group to add the member to
     * @param user The user to add as member
     * @param role The role to assign to the user
     * @return The created group membership
     */
    GroupMember addMember(ResearchGroup group, User user, GroupRole role);

    /**
     * Removes a member from a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user to remove
     */
    void removeMember(UUID groupId, UUID userId);

    /**
     * Checks if a user has a specific role in a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user
     * @param role The role to check
     * @return true if the user has the role, false otherwise
     */
    boolean hasRole(UUID groupId, UUID userId, GroupRole role);

    /**
     * Checks if a user is a member of a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user
     * @return true if the user is a member, false otherwise
     */
    boolean isMember(UUID groupId, UUID userId);
}