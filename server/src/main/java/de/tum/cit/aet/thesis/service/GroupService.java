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
     * This method fetches all groups regardless of the user's permissions.
     * The returned groups contain basic information without member details.
     *
     * @return List of all groups in the system
     * @throws ServiceException if there's an error accessing the database
     * @see Group
     * @see GroupDto
     */
    @Transactional(readOnly = true)
    public List<Group> getAllGroups() {
        log.debug("Fetching all groups");
        return groupRepository.findAll();
    }

    /**
     * Retrieves a group by its unique identifier.
     * This method performs a direct database lookup using the group's UUID.
     * The returned group includes all its basic information but not its members.
     *
     * @param groupId The UUID of the group to retrieve, must not be null
     * @return The group with the specified ID
     * @throws ResourceNotFoundException if no group exists with the given ID
     * @throws IllegalArgumentException if groupId is null
     * @throws ServiceException if there's an error accessing the database
     * @see Group
     */
    @Transactional(readOnly = true)
    public Group getGroupById(@NotNull UUID groupId) {
        log.debug("Fetching group with ID: {}", groupId);
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + groupId));
    }

    /**
     * Retrieves a group by its unique slug identifier.
     * A slug is a URL-friendly version of the group name used for routing and identification.
     * The slug must be unique across all groups in the system.
     *
     * @param slug The unique slug identifier of the group, must not be null or empty
     * @return The group with the specified slug
     * @throws ResourceNotFoundException if no group exists with the given slug
     * @throws IllegalArgumentException if slug is null or empty
     * @throws ServiceException if there's an error accessing the database
     * @see Group
     */
    @Transactional(readOnly = true)
    public Group getGroupBySlug(@NotNull String slug) {
        log.debug("Fetching group with slug: {}", slug);
        return groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
    }

    /**
     * Creates a new group with the specified creator as the group administrator.
     * This method performs the following operations:
     * 1. Validates the group data and checks for slug uniqueness
     * 2. Creates the group entity with provided details
     * 3. Creates a group member entry for the creator with GROUP_ADMIN role
     * 
     * Rate limited to prevent abuse: 10 group creations per hour per user.
     *
     * @param groupDto The group data transfer object containing group details (name, slug, description, etc.)
     * @param creator The user creating the group who will become the group admin
     * @return The newly created group entity with generated ID
     * @throws ResourceAlreadyExistsException if a group with the same slug already exists
     * @throws ValidationException if the group data is invalid
     * @throws IllegalArgumentException if groupDto or creator is null
     * @throws ServiceException if there's an error during group creation
     * @see GroupDto
     * @see Group
     * @see GroupRole
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
     * This method performs the following operations:
     * 1. Validates the requester has GROUP_ADMIN permissions
     * 2. Checks if the new slug (if changed) is available
     * 3. Updates all group fields from the DTO
     * 
     * Rate limited to prevent abuse: 20 updates per hour per group.
     *
     * @param groupId The UUID of the group to update, must not be null
     * @param groupDto The group data transfer object containing updated details
     * @return The updated group entity
     * @throws ResourceNotFoundException if the group is not found
     * @throws ResourceAlreadyExistsException if the new slug is already in use by another group
     * @throws ValidationException if the updated group data is invalid
     * @throws IllegalArgumentException if groupId or groupDto is null
     * @throws AccessDeniedException if the requester doesn't have GROUP_ADMIN role
     * @throws ServiceException if there's an error during update
     * @see GroupDto
     * @see Group
     * @see GroupPermissionService#validateGroupAdmin
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
     * This method performs the following operations:
     * 1. Validates the image file (size, format, dimensions)
     * 2. Verifies the requester has GROUP_ADMIN permissions
     * 3. Saves the logo file using UploadService
     * 4. Updates the group's logo path
     * 
     * Rate limited to prevent abuse: 5 updates per minute per group.
     * Maximum file size: 5MB
     * Supported formats: PNG, JPEG, GIF
     *
     * @param groupId The UUID of the group, must not be null
     * @param logoData The binary data of the logo image
     * @param contentType The MIME type of the logo (e.g., "image/png", "image/jpeg")
     * @throws ResourceNotFoundException if the group is not found
     * @throws BadRequestException if the file validation fails
     * @throws IllegalArgumentException if any parameter is null
     * @throws AccessDeniedException if the requester doesn't have GROUP_ADMIN role
     * @throws ServiceException if there's an error during logo upload or update
     * @see FileValidator#validateImage
     * @see UploadService#saveGroupLogo
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
     * This method returns all users associated with the group and their roles.
     * The requester must be a member of the group to access this information.
     *
     * @param groupId The UUID of the group to get members for, must not be null
     * @return List of group members with their roles
     * @throws ResourceNotFoundException if the group is not found
     * @throws IllegalArgumentException if groupId is null
     * @throws AccessDeniedException if the requester is not a group member
     * @throws ServiceException if there's an error accessing the database
     * @see GroupMember
     * @see GroupRole
     * @see GroupPermissionService#validateGroupMember
     */
    @Transactional(readOnly = true)
    public List<GroupMember> getGroupMembers(@NotNull UUID groupId) {
        log.debug("Fetching members for group with ID: {}", groupId);
        groupPermissionService.validateGroupMember(groupId);
        return groupMemberRepository.findByGroupId(groupId);
    }

    /**
     * Adds a new member to a group with specified role.
     * This method can only be executed by group administrators.
     * If the user is already a member, their role will not be updated.
     *
     * @param groupId The UUID of the group, must not be null
     * @param userId The UUID of the user to add, must not be null
     * @param role The role to assign to the user, must not be null
     * @return The created group member entity
     * @throws ResourceNotFoundException if the group or user is not found
     * @throws IllegalArgumentException if any parameter is null
     * @throws AccessDeniedException if the requester doesn't have GROUP_ADMIN role
     * @throws ResourceAlreadyExistsException if the user is already a member
     * @throws ServiceException if there's an error during member addition
     * @see GroupMember
     * @see GroupRole
     * @see GroupPermissionService#validateGroupAdmin
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
     * This method can only be executed by group administrators.
     * If the user is not a member, the operation completes silently.
     * Group administrators cannot remove themselves if they are the last admin.
     *
     * @param groupId The UUID of the group, must not be null
     * @param userId The UUID of the user to remove, must not be null
     * @throws IllegalArgumentException if groupId or userId is null
     * @throws AccessDeniedException if the requester doesn't have GROUP_ADMIN role
     * @throws BusinessLogicException if attempting to remove the last admin
     * @throws ServiceException if there's an error during member removal
     * @see GroupMember
     * @see GroupPermissionService#validateGroupAdmin
     */
    @Transactional
    public void removeGroupMember(@NotNull UUID groupId, @NotNull UUID userId) {
        log.info("Removing user {} from group {}", userId, groupId);
        groupPermissionService.validateGroupAdmin(groupId);
        groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .ifPresent(groupMemberRepository::delete);
    }
}
