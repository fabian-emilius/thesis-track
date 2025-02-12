package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.GroupMember;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service responsible for managing group-related permissions and access control.
 * Provides centralized permission checking functionality for group operations.
 */
@Service
@RequiredArgsConstructor
public class GroupPermissionService {
    private final GroupMemberRepository groupMemberRepository;
    private final AuthenticationService authenticationService;

    /**
     * Validates if the current user has the specified permission for a group.
     * @param groupId The UUID of the group to check
     * @param permissionCheck The permission checking function to apply
     * @param errorMessage The error message to display if validation fails
     */
    private void validatePermission(UUID groupId, Function<UUID, Boolean> permissionCheck, String errorMessage) {
        if (!permissionCheck.apply(groupId)) {
            throw new AccessDeniedException(errorMessage);
        }
    }

    /**
     * Validates if the current user is a member of the specified group.
     * @param groupId The UUID of the group to check
     * @throws AccessDeniedException if the user is not a member
     */
    @Transactional(readOnly = true)
    public void validateGroupMember(UUID groupId) {
        validatePermission(groupId, this::isGroupMember, "User is not a member of this group");
    }

    /**
     * Validates if the current user is an admin of the specified group.
     * @param groupId The UUID of the group to check
     * @throws AccessDeniedException if the user is not an admin
     */
    @Transactional(readOnly = true)
    public void validateGroupAdmin(UUID groupId) {
        validatePermission(groupId, this::isGroupAdmin, "User is not an admin of this group");
    }

    /**
     * Validates if the current user is either a supervisor or admin of the specified group.
     * @param groupId The UUID of the group to check
     * @throws AccessDeniedException if the user is neither a supervisor nor admin
     */
    @Transactional(readOnly = true)
    public void validateSupervisorOrAdmin(UUID groupId) {
        validatePermission(groupId, this::isSupervisorOrAdmin, "User must be a supervisor or admin");
    }

    /**
     * Checks if the current user is a member of the specified group.
     * @param groupId The UUID of the group to check
     * @return true if the user is a member or system admin, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isGroupMember(UUID groupId) {
        return checkPermission(groupId, member -> true);
    }

    /**
     * Checks if the current user is an admin of the specified group.
     * @param groupId The UUID of the group to check
     * @return true if the user is a group admin or system admin, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isGroupAdmin(UUID groupId) {
        return checkPermission(groupId, GroupMember::isAdmin);
    }

    /**
     * Checks if the current user is either a supervisor or admin of the specified group.
     * @param groupId The UUID of the group to check
     * @return true if the user is a supervisor, group admin, or system admin, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isSupervisorOrAdmin(UUID groupId) {
        return checkPermission(groupId, 
            member -> member.isAdmin() || member.getRole() == GroupRole.SUPERVISOR);
    }

    /**
     * Generic permission checking method that applies the specified check function.
     * @param groupId The UUID of the group to check
     * @param checkFunction The function to apply to the GroupMember if found
     * @return true if the user has the required permission or is system admin, false otherwise
     */
    private boolean checkPermission(UUID groupId, Function<GroupMember, Boolean> checkFunction) {
        if (authenticationService.isAdmin()) {
            return true;
        }
        return getCurrentUserGroupMember(groupId)
                .map(checkFunction)
                .orElse(false);
    }

    /**
     * Retrieves the GroupMember entity for the current user in the specified group.
     * @param groupId The UUID of the group
     * @return Optional containing the GroupMember if found, empty otherwise
     */
    private Optional<GroupMember> getCurrentUserGroupMember(UUID groupId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = authenticationService.getCurrentUserId();
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
    }

    public boolean hasGroupAccess(Authentication authentication) {
        return isGroupMember(getGroupIdFromRequest()) || authenticationService.isAdmin();
    }

    public boolean hasThesisAccess(Authentication authentication) {
        return isGroupMember(getGroupIdFromRequest()) || authenticationService.isAdmin();
    }

    public boolean hasPresentationAccess(Authentication authentication) {
        return isGroupMember(getGroupIdFromRequest()) || authenticationService.isAdmin();
    }

    private UUID getGroupIdFromRequest() {
        String groupId = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest().getParameter("groupId");
        return groupId != null ? UUID.fromString(groupId) : null;
    }

    public void validateSameGroup(UUID group1, UUID group2) {
        if (!group1.equals(group2)) {
            throw new AccessDeniedException("Groups do not match");
        }
    }
}
