package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.ThesisDto;
import de.tum.cit.aet.thesis.dto.TopicDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupService extends BaseGroupService {
    private final GroupRepository groupRepository;
    private final TopicService topicService;
    private final ThesisService thesisService;

    public GroupService(
            GroupRepository groupRepository,
            UserGroupRepository userGroupRepository,
            AuthenticationService authenticationService,
            TopicService topicService,
            ThesisService thesisService) {
        super(groupRepository, userGroupRepository, authenticationService);
        this.groupRepository = groupRepository;
        this.topicService = topicService;
        this.thesisService = thesisService;
    }

    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return userGroupRepository.findByUserId(authenticationService.getCurrentUser().getUserId())
                .stream()
                .map(userGroup -> convertToDto(userGroup.getGroup()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupDto getGroup(UUID groupId) {
        validateGroupAccess(groupId);
        return convertToDto(findGroupById(groupId));
    }

    @Transactional
    public GroupDto createGroup(String name, String description) {
        Group group = new Group();
        group.setGroupId(UUID.randomUUID());
        group.setName(name);
        group.setDescription(description);
        group = groupRepository.save(group);

        // Add creator as admin
        UserGroup userGroup = new UserGroup();
        userGroup.setId(new UserGroupId(authenticationService.getCurrentUser().getUserId(), group.getGroupId()));
        userGroup.setUser(authenticationService.getCurrentUser());
        userGroup.setGroup(group);
        userGroup.setRole("ADMIN");
        userGroupRepository.save(userGroup);

        return convertToDto(group);
    }

    @Transactional
    public GroupDto updateGroup(UUID groupId, String name, String description) {
        validateGroupAdmin(groupId);
        Group group = findGroupById(groupId);
        group.setName(name);
        group.setDescription(description);
        return convertToDto(groupRepository.save(group));
    }

    @Transactional(readOnly = true)
    public List<TopicDto> getGroupTopics(UUID groupId) {
        validateGroupAccess(groupId);
        return topicService.getTopicsByGroup(groupId);
    }

    @Transactional(readOnly = true)
    public List<ThesisDto> getGroupTheses(UUID groupId) {
        validateGroupAccess(groupId);
        return thesisService.getThesesByGroup(groupId);
    }

    

    private Group findGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
    }

    private GroupDto convertToDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setGroupId(group.getGroupId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setCreatedAt(group.getCreatedAt());
        dto.setUpdatedAt(group.getUpdatedAt());
        return dto;
    }
}