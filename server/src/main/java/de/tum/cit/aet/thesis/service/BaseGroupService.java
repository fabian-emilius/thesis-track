package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseGroupService {
    protected final UserGroupRepository userGroupRepository;
    protected final AuthenticationService authenticationService;

    /**
     * Validates if the current user has access to the specified group
     *
     * @param groupId The group ID to check access for
     * @throws AccessDeniedException if the user doesn't have access
     */
    protected void validateGroupAccess(UUID groupId) {
        if (!hasGroupAccess(groupId)) {
            throw new AccessDeniedException("You don't have access to this group");
        }
    }

    /**
     * Validates if the current user has admin access to the specified group
     *
     * @param groupId The group ID to check admin access for
     * @throws AccessDeniedException if the user doesn't have admin access
     */
    protected void validateGroupAdmin(UUID groupId) {
        if (!isGroupAdmin(groupId)) {
            throw new AccessDeniedException("You don't have admin access to this group");
        }
    }

    /**
     * Checks if the current user has access to the specified group
     *
     * @param groupId The group ID to check access for
     * @return true if the user has access, false otherwise
     */
    protected boolean hasGroupAccess(UUID groupId) {
        return userGroupRepository.findByUserIdAndGroupId(
                authenticationService.getCurrentUser().getUserId(), groupId) != null;
    }

    /**
     * Checks if the current user is an admin of the specified group
     *
     * @param groupId The group ID to check admin status for
     * @return true if the user is an admin, false otherwise
     */
    protected boolean isGroupAdmin(UUID groupId) {
        UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(
                authenticationService.getCurrentUser().getUserId(), groupId);
        return userGroup != null && "ADMIN".equals(userGroup.getRole());
    }
}