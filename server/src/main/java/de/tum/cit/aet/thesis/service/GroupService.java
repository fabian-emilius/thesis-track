package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public GroupService(GroupRepository groupRepository,
                       UserGroupRepository userGroupRepository,
                       AuthenticationService authenticationService) {
        this.groupRepository = groupRepository;
        this.userGroupRepository = userGroupRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Group getGroupById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional(readOnly = true)
    public Group getGroupBySlug(String slug) {
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    @Transactional
    public Group createGroup(Group group) {
        if (groupRepository.existsBySlug(group.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }
        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroup(UUID id, Group updatedGroup) {
        Group group = getGroupById(id);

        group.setName(updatedGroup.getName());
        group.setDescription(updatedGroup.getDescription());
        group.setWebsiteUrl(updatedGroup.getWebsiteUrl());
        
        if (!group.getSlug().equals(updatedGroup.getSlug()) && 
            groupRepository.existsBySlug(updatedGroup.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }
        group.setSlug(updatedGroup.getSlug());

        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroupSettings(UUID id, Map<String, Object> settings) {
        Group group = getGroupById(id);
        group.setSettings(settings);
        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroupLogo(UUID id, String logoUrl) {
        Group group = getGroupById(id);
        group.setLogoUrl(logoUrl);
        return groupRepository.save(group);
    }

    @Transactional
    public void addUserToGroup(UUID groupId, UUID userId, GroupRole role) {
        Group group = getGroupById(groupId);
        User user = authenticationService.getUserById(userId);

        UserGroup userGroup = new UserGroup();
        userGroup.setId(new UserGroupId(userId, groupId));
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setRole(role);

        userGroupRepository.save(userGroup);
    }

    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        UserGroupId id = new UserGroupId(userId, groupId);
        userGroupRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean isUserInGroup(UUID groupId, UUID userId) {
        UserGroupId id = new UserGroupId(userId, groupId);
        return userGroupRepository.existsById(id);
    }
}