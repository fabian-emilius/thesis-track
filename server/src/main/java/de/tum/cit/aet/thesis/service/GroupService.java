package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    /**
     * Retrieves all groups from the system.
     *
     * @return List of all groups
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        log.debug("Fetching all groups");
        return groupRepository.findAll();
    }

    /**
     * Retrieves a specific group by its ID.
     *
     * @param id The UUID of the group to retrieve
     * @return The requested group
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional(readOnly = true)
    public Group getGroupById(UUID id) {
        log.debug("Fetching group with id: {}", id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }

    /**
     * Creates a new group.
     *
     * @param group The group to create
     * @return The created group
     * @throws ResourceAlreadyExistsException if a group with the same name already exists
     */
    @Transactional
    public Group createGroup(Group group) {
        log.debug("Creating new group: {}", group.getName());
        try {
            return groupRepository.save(group);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create group: {}", e.getMessage());
            throw new ResourceAlreadyExistsException("Group already exists with name: " + group.getName());
        }
    }

    /**
     * Updates an existing group.
     *
     * @param id The UUID of the group to update
     * @param groupDetails The updated group details
     * @return The updated group
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    public Group updateGroup(UUID id, Group groupDetails) {
        log.debug("Updating group with id: {}", id);
        Group group = getGroupById(id);
        
        group.setName(groupDetails.getName());
        group.setDescription(groupDetails.getDescription());
        
        try {
            return groupRepository.save(group);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to update group: {}", e.getMessage());
            throw new ResourceAlreadyExistsException("Group already exists with name: " + groupDetails.getName());
        }
    }

    /**
     * Deletes a group by its ID.
     *
     * @param id The UUID of the group to delete
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    public void deleteGroup(UUID id) {
        log.debug("Deleting group with id: {}", id);
        Group group = getGroupById(id);
        groupRepository.delete(group);
        log.info("Successfully deleted group with id: {}", id);
    }
}