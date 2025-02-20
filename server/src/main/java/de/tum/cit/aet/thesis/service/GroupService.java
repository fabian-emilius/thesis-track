package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.dto.GroupMemberDto;
import de.tum.cit.aet.thesis.dto.GroupSettingsDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.GroupSettings;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.key.GroupMemberId;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import de.tum.cit.aet.thesis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing research groups and their members.
 * Provides functionality for CRUD operations on groups, member management,
 * and group settings configuration.
 */
@Service
@RequiredArgsConstructor
public class GroupService {
    private static final String AUDIT_ACTION_CREATE = "CREATE";
    private static final String AUDIT_ACTION_UPDATE = "UPDATE";
    private static final String AUDIT_ACTION_DELETE = "DELETE";
    private static final String AUDIT_ACTION_UPDATE_SETTINGS = "UPDATE_SETTINGS";
    private static final String AUDIT_ACTION_ADD_MEMBER = "ADD_MEMBER";
    private static final String AUDIT_ACTION_REMOVE_MEMBER = "REMOVE_MEMBER";
    
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final EmailTemplateValidator emailTemplateValidator;
    private final AuditLogService auditLogService;

    /**
     * Retrieves all groups in the system.
     *
     * @return List of all groups as DTOs
     */
    @Transactional(readOnly = true)
    public List<GroupDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupDto::from)
                .toList();
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param id The UUID of the group to retrieve
     * @return The group DTO
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional(readOnly = true)
    public GroupDto getGroupById(UUID id) {
        return groupRepository.findById(id)
                .map(GroupDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
    }

    /**
     * Retrieves a group by its slug identifier.
     *
     * @param slug The unique slug of the group
     * @return The group DTO
     * @throws ResourceNotFoundException if no group is found with the given slug
     */
    @Transactional(readOnly = true)
    public GroupDto getGroupBySlug(String slug) {
        return groupRepository.findBySlug(slug)
                .map(GroupDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
    }

    /**
     * Creates a new research group with the specified details.
     *
     * @param name The name of the group
     * @param slug The unique slug identifier for the group
     * @param description The group's description
     * @param logoUrl The URL to the group's logo
     * @param externalLink The external website link for the group
     * @return The created group DTO
     * @throws ResourceAlreadyExistsException if a group with the same slug already exists
     */
    @Transactional
    public GroupDto createGroup(String name, String slug, String description, String logoUrl, String externalLink) {
        if (groupRepository.existsBySlug(slug)) {
            throw new ResourceAlreadyExistsException("Group with slug " + slug + " already exists");
        }

        Group group = createGroupEntity(name, slug, description, logoUrl, externalLink);
        group = groupRepository.save(group);
        
        auditLogService.logGroupAction(group.getId(), AUDIT_ACTION_CREATE, "Created group: " + name);
        return GroupDto.from(group);
    }

    private Group createGroupEntity(String name, String slug, String description, String logoUrl, String externalLink) {
        Group group = new Group();
        group.setName(name);
        group.setSlug(slug);
        group.setDescription(description);
        group.setLogoUrl(logoUrl);
        group.setExternalLink(externalLink);

        GroupSettings settings = new GroupSettings();
        settings.setGroup(group);
        group.setSettings(settings);
        
        return group;
    }

    /**
     * Updates an existing group's information.
     *
     * @param id The ID of the group to update
     * @param name The new name for the group
     * @param description The new description
     * @param logoUrl The new logo URL
     * @param externalLink The new external link
     * @return The updated group DTO
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    public GroupDto updateGroup(UUID id, String name, String description, String logoUrl, String externalLink) {
        Group group = findGroupById(id);
        updateGroupFields(group, name, description, logoUrl, externalLink);
        group = groupRepository.save(group);
        
        auditLogService.logGroupAction(group.getId(), AUDIT_ACTION_UPDATE, "Updated group information");
        return GroupDto.from(group);
    }

    private void updateGroupFields(Group group, String name, String description, String logoUrl, String externalLink) {
        group.setName(name);
        group.setDescription(description);
        group.setLogoUrl(logoUrl);
        group.setExternalLink(externalLink);
    }

    /**
     * Updates the settings for a specific group.
     *
     * @param groupId The ID of the group to update
     * @param settingsDto The new settings to apply
     * @return The updated group settings
     * @throws ResourceNotFoundException if the group is not found
     * @throws ResourceInvalidParametersException if any email template is invalid
     */
    @Transactional
    public GroupSettingsDto updateGroupSettings(UUID groupId, GroupSettingsDto settingsDto) {
        Group group = findGroupById(groupId);
        validateEmailTemplates(settingsDto);
        
        GroupSettings settings = group.getSettings();
        updateSettings(settings, settingsDto);

        group = groupRepository.save(group);
        auditLogService.logGroupAction(groupId, AUDIT_ACTION_UPDATE_SETTINGS, "Updated group settings");
        return GroupSettingsDto.from(group.getSettings());
    }

    private void validateEmailTemplates(GroupSettingsDto settingsDto) {
        if (!emailTemplateValidator.isValid(settingsDto.getAcceptanceEmailTemplate())) {
            throw new ResourceInvalidParametersException("Invalid acceptance email template");
        }
        if (!emailTemplateValidator.isValid(settingsDto.getPostAcceptanceInstructions())) {
            throw new ResourceInvalidParametersException("Invalid post-acceptance instructions template");
        }
        if (!emailTemplateValidator.isValid(settingsDto.getEmailFooter())) {
            throw new ResourceInvalidParametersException("Invalid email footer template");
        }
    }

    private void updateSettings(GroupSettings settings, GroupSettingsDto settingsDto) {
        settings.setAcceptanceEmailTemplate(settingsDto.getAcceptanceEmailTemplate());
        settings.setPostAcceptanceInstructions(settingsDto.getPostAcceptanceInstructions());
        settings.setEmailFooter(settingsDto.getEmailFooter());
    }

    /**
     * Deletes a group and all its associated members.
     *
     * @param id The ID of the group to delete
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    public void deleteGroup(UUID id) {
        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Group not found with ID: " + id);
        }
        
        auditLogService.logGroupAction(id, AUDIT_ACTION_DELETE, "Deleted group");
        groupMemberRepository.deleteByGroupId(id);
        groupRepository.deleteById(id);
    }

    /**
     * Retrieves all members of a specific group.
     *
     * @param groupId The ID of the group
     * @return List of group members as DTOs
     */
    @Transactional(readOnly = true)
    public List<GroupMemberDto> getGroupMembers(UUID groupId) {
        return groupMemberRepository.findByGroupId(groupId).stream()
                .map(GroupMemberDto::from)
                .toList();
    }

    /**
     * Adds a new member to a group with the specified role.
     *
     * @param groupId The ID of the group to add the member to
     * @param userId The ID of the user to add as a member
     * @param role The role to assign to the member
     * @return The created group member DTO
     * @throws ResourceNotFoundException if either the group or user is not found
     */
    @Transactional
    public GroupMemberDto addGroupMember(UUID groupId, UUID userId, String role) {
        Group group = findGroupById(groupId);
        User user = findUserById(userId);

        GroupMember member = createGroupMember(group, user, role);
        member = groupMemberRepository.save(member);
        
        auditLogService.logGroupAction(groupId, AUDIT_ACTION_ADD_MEMBER, 
            String.format("Added member %s with role %s", userId, role));

        return GroupMemberDto.from(member);
    }

    private GroupMember createGroupMember(Group group, User user, String role) {
        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(group.getId(), user.getId()));
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);
        return member;
    }

    /**
     * Removes a member from a group.
     *
     * @param groupId The ID of the group
     * @param userId The ID of the user to remove
     */
    @Transactional
    public void removeGroupMember(UUID groupId, UUID userId) {
        if (!groupMemberRepository.existsById(new GroupMemberId(groupId, userId))) {
            throw new ResourceNotFoundException("Group member not found");
        }
        
        auditLogService.logGroupAction(groupId, AUDIT_ACTION_REMOVE_MEMBER, 
            String.format("Removed member %s", userId));
        groupMemberRepository.deleteById(new GroupMemberId(groupId, userId));
    }

    /**
     * Checks if a user is a member of a specific group.
     *
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @return true if the user is a member of the group, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isUserInGroup(UUID userId, UUID groupId) {
        return groupMemberRepository.findById(new GroupMemberId(groupId, userId)).isPresent();
    }

    /**
     * Gets the role of a user in a specific group.
     *
     * @param userId The ID of the user
     * @param groupId The ID of the group
     * @return The user's role in the group, or null if the user is not a member
     */
    @Transactional(readOnly = true)
    public String getUserGroupRole(UUID userId, UUID groupId) {
        return groupMemberRepository.findById(new GroupMemberId(groupId, userId))
                .map(GroupMember::getRole)
                .orElse(null);
    }

    /**
     * Helper method to find a group by ID and throw a standardized exception if not found.
     *
     * @param groupId The ID of the group to find
     * @return The found group entity
     * @throws ResourceNotFoundException if the group is not found
     */
    private Group findGroupById(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
    }

    /**
     * Helper method to find a user by ID and throw a standardized exception if not found.
     *
     * @param userId The ID of the user to find
     * @return The found user entity
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }
}
