package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for managing and validating group access permissions.
 * This service implements hierarchical access control for different user roles
 * and their associated group permissions.
 *
 * @author Thesis Management System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupAccessService {
    private final UserGroupRepository userGroupRepository;

    /**
     * Validates if a user has access to a specific group.
     * Throws AccessDeniedException if the user doesn't have the required permissions.
     *
     * @param user    the user requesting access
     * @param groupId the UUID of the group to access
     * @throws AccessDeniedException if the user doesn't have access to the group
     */
    public void validateGroupAccess(User user, UUID groupId) {
        log.debug("Validating group access for user {} to group {}", user.getId(), groupId);
        if (!hasGroupAccess(user, groupId)) {
            log.warn("Access denied for user {} to group {}", user.getId(), groupId);
            throw new AccessDeniedException("User does not have access to this group");
        }
        log.debug("Access granted for user {} to group {}", user.getId(), groupId);
    }

    /**
     * Validates if a user has access to a specific group.
     * Throws AccessDeniedException if the user doesn't have the required permissions.
     *
     * @param user  the user requesting access
     * @param group the group to access
     * @throws AccessDeniedException if the user doesn't have access to the group
     */
    public void validateGroupAccess(User user, UserGroup group) {
        validateGroupAccess(user, group.getId());
    }

    /**
     * Checks if a user has access to a specific group based on their role
     * and group membership.
     *
     * @param user    the user requesting access
     * @param groupId the UUID of the group to check
     * @return true if the user has access, false otherwise
     */
    public boolean hasGroupAccess(User user, UUID groupId) {
        if (user == null || groupId == null) {
            log.warn("Null user or groupId in hasGroupAccess check");
            return false;
        }

        // Admin has access to all groups
        if (user.hasGroup("admin")) {
            log.debug("Admin access granted for user {} to group {}", user.getId(), groupId);
            return true;
        }

        // Check if user is directly associated with the group
        boolean isGroupMember = userGroupRepository.existsByUserIdAndGroupId(user.getId(), groupId);
        if (isGroupMember) {
            log.debug("Direct group membership found for user {} in group {}", user.getId(), groupId);
            return true;
        }

        // Supervisors have access to their assigned groups and subgroups
        if (user.hasGroup("supervisor")) {
            boolean hasSupervisorAccess = userGroupRepository.isSupervisorOfGroup(user.getId(), groupId);
            log.debug("Supervisor access check for user {} to group {}: {}", user.getId(), groupId, hasSupervisorAccess);
            return hasSupervisorAccess;
        }

        // Advisors have access only to their specifically assigned groups
        if (user.hasGroup("advisor")) {
            boolean hasAdvisorAccess = userGroupRepository.isAdvisorOfGroup(user.getId(), groupId);
            log.debug("Advisor access check for user {} to group {}: {}", user.getId(), groupId, hasAdvisorAccess);
            return hasAdvisorAccess;
        }

        log.debug("No access granted for user {} to group {}", user.getId(), groupId);
        return false;
    }

    /**
     * Checks if a user has access to a specific group based on their role
     * and group membership.
     *
     * @param user  the user requesting access
     * @param group the group to check
     * @return true if the user has access, false otherwise
     */
    public boolean hasGroupAccess(User user, UserGroup group) {
        return hasGroupAccess(user, group.getId());
    }
}