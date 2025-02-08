package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return groupRepository.findByActiveTrue()
                .stream()
                .map(GroupDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupDto getGroupById(Long id) {
        return groupRepository.findById(id)
                .map(GroupDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional
    public GroupDto createGroup(String name, String description) {
        if (groupRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyExistsException("Group with this name already exists");
        }

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setActive(true);

        return GroupDto.from(groupRepository.save(group));
    }

    @Transactional
    public GroupDto updateGroup(Long id, String name, String description) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        groupRepository.findByName(name)
                .filter(g -> !g.getId().equals(id))
                .ifPresent(g -> {
                    throw new ResourceAlreadyExistsException("Group with this name already exists");
                });

        group.setName(name);
        group.setDescription(description);

        return GroupDto.from(groupRepository.save(group));
    }

    @Transactional
    public void deactivateGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        group.setActive(false);
        groupRepository.save(group);
    }

    @Transactional
    public void activateGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        group.setActive(true);
        groupRepository.save(group);
    }
}