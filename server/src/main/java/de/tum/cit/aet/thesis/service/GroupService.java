package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.dto.GroupDto;
import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.ResourceAlreadyExistsException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@RateLimitGroup(name = "group-service")
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupPermissionService groupPermissionService;
    private final UploadService uploadService;

    /**
     * Retrieves all groups in the system.
     *
     * @return List of all groups
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        log.debug("Fetching all groups");
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by its ID.
     *
     * @param groupId The UUID of the group to retrieve
     * @return The group with the specified ID
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional(readOnly = true)
    public Group getGroupById(@NotNull UUID groupId) {
        log.debug("Fetching group with ID: {}", groupId);
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
    }

    /**
     * Retrieves a group by its slug.
     *
     * @param slug The unique slug identifier of the group
     * @return The group with the specified slug
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional(readOnly = true)
    public Group getGroupBySlug(@NotNull String slug) {
        log.debug("Fetching group with slug: {}", slug);
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
    }

    /**
     * Creates a new group with the specified creator as admin.
     *
     * @param groupDto The group data transfer object containing group details
     * @param creator The user creating the group
     * @return The created group
     * @throws ResourceAlreadyExistsException if a group with the same slug already exists
     */
    @Transactional
    @RateLimit(permits = 10, duration = 1, unit = TimeUnit.HOURS)
    public Group createGroup(@NotNull @Valid GroupDto groupDto, @NotNull User creator) {
        log.info("Creating new group with slug: {} by user: {}", groupDto.getSlug(), creator.getId());
        if (groupRepository.existsBySlug(groupDto.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }

        Group group = new Group();
        updateGroupFromDto(group, groupDto);
        group = groupRepository.save(group);

        // Create group member entry for creator as GROUP_ADMIN
        GroupMember member = new GroupMember();
        member.setGroupId(group.getId());
        member.setUserId(creator.getId());
        member.setRole(GroupRole.GROUP_ADMIN);
        groupMemberRepository.save(member);

        return group;
    }

    /**
     * Updates an existing group's information.
     *
     * @param groupId The UUID of the group to update
     * @param groupDto The group data transfer object containing updated details
     * @return The updated group
     * @throws ResourceNotFoundException if the group is not found
     * @throws ResourceAlreadyExistsException if the new slug is already in use
     */
    @Transactional
    @RateLimit(permits = 20, duration = 1, unit = TimeUnit.HOURS)
    public Group updateGroup(@NotNull UUID groupId, @NotNull @Valid GroupDto groupDto) {
        log.info("Updating group with ID: {}", groupId);
        Group group = getGroupById(groupId);
        groupPermissionService.validateGroupAdmin(group.getId());

        if (!group.getSlug().equals(groupDto.getSlug()) && 
            groupRepository.existsBySlug(groupDto.getSlug())) {
            throw new ResourceAlreadyExistsException("Group with this slug already exists");
        }

        updateGroupFromDto(group, groupDto);
        return groupRepository.save(group);
    }

    /**
     * Updates the logo for a group.
     *
     * @param groupId The UUID of the group
     * @param logoData The binary data of the logo
     * @param contentType The MIME type of the logo
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    @RateLimit(permits = 5, duration = 1, unit = TimeUnit.MINUTES)
    public void updateGroupLogo(@NotNull UUID groupId, @NotNull byte[] logoData, @NotNull String contentType) {
        log.info("Updating logo for group with ID: {}", groupId);
        try {
            FileValidator.validateImage(logoData, contentType, 5_000_000L); // 5MB limit
            Group group = getGroupById(groupId);
            groupPermissionService.validateGroupAdmin(group.getId());

            String logoPath = uploadService.saveGroupLogo(groupId, logoData, contentType);
            group.setLogoPath(logoPath);
            groupRepository.save(group);
            log.info("Successfully updated logo for group: {}", groupId);
        } catch (FileValidationException e) {
            log.error("File validation failed for group logo update: {}", e.getMessage());
            throw new BadRequestException("Invalid logo file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error updating group logo: {}", e.getMessage(), e);
            throw new ServiceException("Failed to update group logo", e);
        }
    }

    private void updateGroupFromDto(Group group, GroupDto dto) {
        group.setName(dto.getName());
        group.setSlug(dto.getSlug());
        group.setDescription(dto.getDescription());
        group.setLink(dto.getLink());
        group.setMailFooter(dto.getMailFooter());
        group.setAcceptanceText(dto.getAcceptanceText());
    }

    /**
     * Retrieves all members of a group.
     *
     * @param groupId The UUID of the group
     * @return List of group members
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional(readOnly = true)
    public List<GroupMember> getGroupMembers(@NotNull UUID groupId) {
        log.debug("Fetching members for group with ID: {}", groupId);
        groupPermissionService.validateGroupMember(groupId);
        return groupMemberRepository.findByGroupId(groupId);
    }

    /**
     * Adds a new member to a group with specified role.
     *
     * @param groupId The UUID of the group
     * @param userId The UUID of the user to add
     * @param role The role to assign to the user
     * @return The created group member entity
     * @throws ResourceNotFoundException if the group is not found
     */
    @Transactional
    public GroupMember addGroupMember(@NotNull UUID groupId, @NotNull UUID userId, @NotNull GroupRole role) {
        log.info("Adding user {} to group {} with role {}", userId, groupId, role);
        groupPermissionService.validateGroupAdmin(groupId);

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(role);
        return groupMemberRepository.save(member);
    }

    /**
     * Removes a member from a group.
     *
     * @param groupId The UUID of the group
     * @param userId The UUID of the user to remove
     */
    @Transactional
    public void removeGroupMember(@NotNull UUID groupId, @NotNull UUID userId) {
        log.info("Removing user {} from group {}", userId, groupId);
        groupPermissionService.validateGroupAdmin(groupId);
        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .ifPresent(groupMemberRepository::delete);
    }
}
