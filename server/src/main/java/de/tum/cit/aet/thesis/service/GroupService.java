package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional
    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroup(UUID id, Group updatedGroup) {
        Group group = getGroupById(id);
        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        return groupRepository.save(group);
    }
}
