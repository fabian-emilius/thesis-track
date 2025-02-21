package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupRole;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.GroupRoleRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupRoleRepository groupRoleRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupRoleRepository groupRoleRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupRoleRepository = groupRoleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Group createGroup(User creator, String slug, String name, String description, String logoUrl, String websiteUrl, String mailFooter, String acceptanceText, String acceptanceInstructions) {
        if (groupRepository.existsBySlug(slug)) {
            throw new ResourceInvalidParametersException("A group with the given slug already exists.");
        }

        Group group = new Group();
        group.setSlug(slug);
        group.setName(name);
        group.setDescription(description);
        group.setLogoUrl(logoUrl);
        group.setWebsiteUrl(websiteUrl);
        group.setMailFooter(mailFooter);
        group.setAcceptanceText(acceptanceText);
        group.setAcceptanceInstructions(acceptanceInstructions);
        group.setCreatedAt(Instant.now());
        group.setUpdatedAt(Instant.now());
        group.setCreatedBy(creator);

        return groupRepository.save(group);
    }

    @Transactional
    public Group updateGroup(UUID groupId, String name, String description, String logoUrl, String websiteUrl, String mailFooter, String acceptanceText, String acceptanceInstructions) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        group.setName(name);
        group.setDescription(description);
        group.setLogoUrl(logoUrl);
        group.setWebsiteUrl(websiteUrl);
        group.setMailFooter(mailFooter);
        group.setAcceptanceText(acceptanceText);
        group.setAcceptanceInstructions(acceptanceInstructions);
        group.setUpdatedAt(Instant.now());

        return groupRepository.save(group);
    }

    public Group findGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
    }

    public Group findGroupBySlug(String slug) {
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
    }

    @Transactional
    public GroupRole addGroupRole(UUID groupId, UUID userId, String roleName) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        GroupRole groupRole = new GroupRole();
        groupRole.setGroup(group);
        groupRole.setUser(user);
        groupRole.setRole(roleName);
        groupRole.setCreatedAt(Instant.now());
        groupRole.setUpdatedAt(Instant.now());

        return groupRoleRepository.save(groupRole);
    }

    @Transactional
    public void removeGroupRole(UUID groupId, UUID userId) {
        GroupRole groupRole = groupRoleRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group role not found for groupId: " + groupId + " and userId: " + userId));

        groupRoleRepository.delete(groupRole);
    }

    public boolean checkUserPermission(UUID groupId, UUID userId, String requiredRole) {
        List<GroupRole> roles = groupRoleRepository.findByGroupId(groupId);

        return roles.stream()
                .anyMatch(role -> role.getUser().getId().equals(userId) && role.getRole().equalsIgnoreCase(requiredRole));
    }
}
