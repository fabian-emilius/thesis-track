package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing groups and group memberships.
 * Handles CRUD operations for groups and manages group-user relationships.
 */
@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final AuthenticationService authenticationService;

    /**
     * Retrieves all groups in the system.
     * @return List of all groups
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        log.debug("Fetching all groups");
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by its ID.
     * @param id The UUID of the group
     * @return The group if found
     * @throws ResourceNotFoundException if group not found
     */
    @Transactional(readOnly = true)
    public Group getGroupById(UUID id) {
        log.debug("Fetching group with id: {}", id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    /**
     * Retrieves a group by its slug.
     * @param slug The unique slug of the group
     * @return The group if found
     * @throws ResourceNotFoundException if group not found
     */
    @Transactional(readOnly = true)
    public Group getGroupBySlug(String slug) {
        log.debug("Fetching group with slug: {}", slug);
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
    }

    /**
     * Creates a new group.
     * @param group The group to create
     * @return The created group
     * @throws ResourceAlreadyExistsException if slug already exists
     * @throws ResourceInvalidParametersException if validation fails
     */
    @Transactional
    public Group createGroup(@Valid Group group) {
        log.info("Creating new group with slug: {}", group.getSlug());
        if (groupRepository.existsBySlug(group.getSlug())) {
            throw new ResourceAlreadyExistsException("Group already exists with slug: " + group.getSlug());
        }
        return groupRepository.save(group);
    }

    /**
     * Updates an existing group.
     * @param id The UUID of the group to update
     * @param groupDetails The updated group details
     * @return The updated group
     * @throws ResourceNotFoundException if group not found
     * @throws ResourceAlreadyExistsException if new slug already exists
     */
    @Transactional
    public Group updateGroup(UUID id, @Valid Group groupDetails) {
        log.info("Updating group with id: {}", id);
        Group group = getGroupById(id);
        
        group.setName(groupDetails.getName());
        group.setDescription(groupDetails.getDescription());
        group.setWebsiteUrl(groupDetails.getWebsiteUrl());
        
        if (groupDetails.getSlug() != null && !groupDetails.getSlug().equals(group.getSlug())) {
            if (groupRepository.existsBySlug(groupDetails.getSlug())) {
                throw new ResourceAlreadyExistsException("Group already exists with slug: " + groupDetails.getSlug());
            }
            group.setSlug(groupDetails.getSlug());
        }
        
        return groupRepository.save(group);
    }

    /**
     * Updates a group's settings.
     * @param id The UUID of the group
     * @param settings The new settings map
     * @return The updated group
     * @throws ResourceNotFoundException if group not found
     */
    @Transactional
    public Group updateGroupSettings(UUID id, Map<String, Object> settings) {
        log.info("Updating settings for group with id: {}", id);
        Group group = getGroupById(id);
        group.setSettings(settings);
        return groupRepository.save(group);
    }

    /**
     * Updates a group's logo URL.
     * @param id The UUID of the group
     * @param logoUrl The new logo URL
     * @return The updated group
     * @throws ResourceNotFoundException if group not found
     */
    @Transactional
    public Group updateGroupLogo(UUID id, String logoUrl) {
        log.info("Updating logo for group with id: {}", id);
        Group group = getGroupById(id);
        group.setLogoUrl(logoUrl);
        return groupRepository.save(group);
    }

    /**
     * Checks if a user is a member of a group.
     * @param groupId The UUID of the group
     * @param userId The UUID of the user
     * @return true if the user is a member of the group
     */
    @Transactional(readOnly = true)
    public boolean isUserInGroup(UUID groupId, UUID userId) {
        return userGroupRepository.existsByGroupIdAndUserId(groupId, userId);
    }

    /**
     * Gets a user's role in a group.
     * @param groupId The UUID of the group
     * @param userId The UUID of the user
     * @return The user's role or null if not a member
     */
    @Transactional(readOnly = true)
    public String getUserGroupRole(UUID groupId, UUID userId) {
        return userGroupRepository.findByGroupIdAndUserId(groupId, userId)
                .map(UserGroup::getRole)
                .orElse(null);
    }

    /**
     * Adds a user to a group with a specific role.
     * @param groupId The UUID of the group
     * @param userId The UUID of the user
     * @param role The role to assign
     * @throws ResourceInvalidParametersException if role is invalid
     */
    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId, String role) {
        if (!GroupRole.isValid(role)) {
            throw new ResourceInvalidParametersException("Invalid group role: " + role);
        }

        UserGroup userGroup = new UserGroup();
        userGroup.getId().setGroupId(groupId);
        userGroup.getId().setUserId(userId);
        userGroup.setRole(role);

        userGroupRepository.save(userGroup);
        log.info("Added user {} to group {} with role {}", userId, groupId, role);
    }
}