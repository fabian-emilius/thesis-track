package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Groups.
 */
@Service
@Slf4j
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * Get all groups.
     * @return list of groups
     */
    public List<Group> getAllGroups() {
        log.debug("Request to get all Groups");
        return groupRepository.findAll();
    }

    /**
     * Get one group by id.
     * @param id the id of the group
     * @return the group
     * @throws ResourceNotFoundException when group is not found
     */
    public Group getGroupById(UUID id) {
        log.debug("Request to get Group : {}", id);
        return groupRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Group not found : {}", id);
                    return new ResourceNotFoundException("Group not found");
                });
    }

    /**
     * Create a new group.
     * @param group the group to create
     * @return the created group
     */
    @Transactional
    public Group createGroup(Group group) {
        log.debug("Request to create Group : {}", group);
        return groupRepository.save(group);
    }

    /**
     * Update a group.
     * @param id the id of the group to update
     * @param updatedGroup the group with updated fields
     * @return the updated group
     * @throws ResourceNotFoundException when group is not found
     */
    @Transactional
    public Group updateGroup(UUID id, Group updatedGroup) {
        log.debug("Request to update Group : {}", id);
        Group group = getGroupById(id);
        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        return groupRepository.save(group);
    }
}
