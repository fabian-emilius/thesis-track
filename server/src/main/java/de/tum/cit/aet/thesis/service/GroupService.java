package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing groups in the system.
 * Handles group creation, updates, and queries while ensuring data consistency
 * and proper validation.
 */
@Service
@Validated
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    /**
     * Retrieves all groups in the system.
     *
     * @return List of all groups
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param id The UUID of the group
     * @return The group if found
     * @throws ResourceNotFoundException if the group doesn't exist
     */
    @Transactional(readOnly = true)
    public Group getGroupById(@NotNull UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * Retrieves a group by its slug.
     *
     * @param slug The unique slug of the group
     * @return The group if found
     * @throws ResourceNotFoundException if the group doesn't exist
     */
    @Transactional(readOnly = true)
    public Group getGroupBySlug(@NotNull String slug) {
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    /**
     * Creates a new group.
     *
     * @param group The group to create
     * @return The created group
     * @throws ResourceAlreadyExistsException if a group with the same slug exists
     * @throws ResourceInvalidParametersException if the group data is invalid
     */
    @Transactional
    public Group createGroup(@NotNull @Valid Group group) {
        validateGroup(group);
        if (groupRepository.existsBySlug(group.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }
        return groupRepository.save(group);
    }

    /**
     * Updates an existing group.
     *
     * @param id The ID of the group to update
     * @param updatedGroup The updated group data
     * @return The updated group
     * @throws ResourceNotFoundException if the group doesn't exist
     * @throws ResourceAlreadyExistsException if the new slug conflicts with an existing group
     * @throws ResourceInvalidParametersException if the group data is invalid
     */
    @Transactional
    public Group updateGroup(@NotNull UUID id, @NotNull @Valid Group updatedGroup) {
        Group group = getGroupById(id);
        validateGroup(updatedGroup);
        
        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setWebsiteUrl(updatedGroup.getWebsiteUrl());
        group.setSettings(updatedGroup.getSettings());
        
        if (!group.getSlug().equals(updatedGroup.getSlug()) && 
            groupRepository.existsBySlug(updatedGroup.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }
        group.setSlug(updatedGroup.getSlug());
        
        return groupRepository.save(group);
    }

    /**
     * Updates a group's logo URL.
     *
     * @param id The ID of the group
     * @param logoUrl The new logo URL
     * @throws ResourceNotFoundException if the group doesn't exist
     */
    @Transactional
    public void updateGroupLogo(@NotNull UUID id, String logoUrl) {
        Group group = getGroupById(id);
        group.setLogoUrl(logoUrl);
        groupRepository.save(group);
    }

    /**
     * Updates a group's settings.
     *
     * @param id The ID of the group
     * @param settings The new settings JSON string
     * @throws ResourceNotFoundException if the group doesn't exist
     */
    @Transactional
    public void updateGroupSettings(@NotNull UUID id, String settings) {
        Group group = getGroupById(id);
        group.setSettings(settings);
        groupRepository.save(group);
    }

    private void validateGroup(Group group) {
        if (group.getName() == null || group.getName().trim().isEmpty()) {
            throw new ResourceInvalidParametersException("Group name is required");
        }
        if (group.getSlug() == null || !group.getSlug().matches("^[a-z0-9-]+$")) {
            throw new ResourceInvalidParametersException("Invalid group slug format");
        }
    }
}