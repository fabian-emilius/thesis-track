package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Centralized service for handling group access control and membership operations.
 * This service reduces code duplication by providing common group-related functionality
 * used across different services.
 */
@Service
public class GroupAccessControl {
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public GroupAccessControl(UserGroupRepository userGroupRepository,
                            GroupRepository groupRepository) {
        this.userGroupRepository = userGroupRepository;
        this.groupRepository = groupRepository;
    }

    /**
     * Checks if a user is a member of a specific group
     *
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @return true if the user is a member of the group
     */
    @Transactional(readOnly = true)
    public boolean isMemberOfGroup(UUID userId, UUID groupId) {
        return userGroupRepository.existsById(new UserGroupId(userId, groupId));
    }

    /**
     * Checks if a user has a specific role in a group
     *
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @param role The role to check
     * @return true if the user has the specified role
     */
    @Transactional(readOnly = true)
    public boolean hasGroupRole(UUID userId, UUID groupId, GroupRole role) {
        return userGroupRepository.findById(new UserGroupId(userId, groupId))
                .map(userGroup -> userGroup.getRole() == role)
                .orElse(false);
    }

    /**
     * Checks if a user has any of the specified roles in a group
     *
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @param roles The roles to check
     * @return true if the user has any of the specified roles
     */
    @Transactional(readOnly = true)
    public boolean hasAnyGroupRole(UUID userId, UUID groupId, GroupRole... roles) {
        return userGroupRepository.findById(new UserGroupId(userId, groupId))
                .map(userGroup -> {
                    for (GroupRole role : roles) {
                        if (userGroup.getRole() == role) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    /**
     * Validates group existence and returns the group
     *
     * @param groupId The ID of the group to validate
     * @return The group if it exists
     * @throws ResourceNotFoundException if the group doesn't exist
     */
    @Transactional(readOnly = true)
    public Group validateAndGetGroup(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * Creates a new user-group association
     *
     * @param user The user to associate
     * @param group The group to associate
     * @param role The role to assign
     * @return The created UserGroup entity
     */
    @Transactional
    public UserGroup createUserGroupAssociation(User user, Group group, GroupRole role) {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(new UserGroupId(user.getId(), group.getId()));
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setRole(role);
        return userGroupRepository.save(userGroup);
    }
}