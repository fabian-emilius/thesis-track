package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.UnauthorizedAccessException;
import de.tum.cit.aet.thesis.repository.TopicRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserGroupRepository userGroupRepository;
    private final TopicRepository topicRepository;
    private final GroupAccessService groupAccessService;

    @Transactional(readOnly = true)
    public Page<UserGroup> getAllGroups(Pageable pageable) {
        return userGroupRepository.findAllByUserHasAccess(groupAccessService.getCurrentUserId(), pageable);
    }

    @Transactional(readOnly = true)
    public UserGroup getGroupById(UUID id) {
        UserGroup group = userGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        if (!groupAccessService.hasAccess(group)) {
            throw new UnauthorizedAccessException("No access to this group");
        }
        return group;
    }

    @Transactional
    public UserGroup createGroup(String name, String description) {
        if (!groupAccessService.canCreateGroup()) {
            throw new UnauthorizedAccessException("No permission to create groups");
        }
        GroupValidator.validateGroupName(name);
        GroupValidator.validateGroupDescription(description);
        UserGroup group = new UserGroup();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(groupAccessService.getCurrentUserId());
        return userGroupRepository.save(group);
    }

    @Transactional
    public UserGroup updateGroup(UUID id, String name, String description) {
        UserGroup group = getGroupById(id);
        if (!groupAccessService.canModifyGroup(group)) {
            throw new UnauthorizedAccessException("No permission to modify this group");
        }
        GroupValidator.validateGroupName(name);
        GroupValidator.validateGroupDescription(description);
        group.setName(name);
        group.setDescription(description);
        return userGroupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(UUID id) {
        UserGroup group = getGroupById(id);
        if (!groupAccessService.canDeleteGroup(group)) {
            throw new UnauthorizedAccessException("No permission to delete this group");
        }
        userGroupRepository.delete(group);
    }

    public boolean hasAccessToGroup(UUID groupId) {
        GroupValidator.validateGroupId(groupId);
        return userGroupRepository.findById(groupId)
                .map(groupAccessService::hasAccess)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Page<Topic> getGroupTopics(UUID groupId, Pageable pageable) {
        UserGroup group = getGroupById(groupId);
        if (!groupAccessService.hasAccess(group)) {
            throw new UnauthorizedAccessException("No access to group topics");
        }
        return topicRepository.findByGroupId(groupId, pageable);
    }
}
