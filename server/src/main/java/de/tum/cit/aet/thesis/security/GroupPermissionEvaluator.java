package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Evaluates group-based permissions for the current user.
 * Used in conjunction with Spring Security's @PreAuthorize annotations
 * to enforce group-level access control.
 */
@Component
@RequiredArgsConstructor
public class GroupPermissionEvaluator {
    private final UserGroupRepository userGroupRepository;

    /**
     * Checks if the current user is an admin of the specified group.
     *
     * @param groupId The UUID of the group to check
     * @return true if the user is a group admin, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isGroupAdmin(UUID groupId) {
        if (groupId == null) {
            return false;
        }
        
        UUID userId = getCurrentUserId();
        return userGroupRepository.findById(new UserGroupId(userId, groupId))
                .map(userGroup -> "admin".equals(userGroup.getRole()))
                .orElse(false);
    }

    /**
     * Checks if the current user is a member of the specified group.
     *
     * @param groupId The UUID of the group to check
     * @return true if the user is a group member, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isGroupMember(UUID groupId) {
        if (groupId == null) {
            return false;
        }
        
        UUID userId = getCurrentUserId();
        return userGroupRepository.findById(new UserGroupId(userId, groupId))
                .isPresent();
    }

    /**
     * Checks if the current user has a specific role in the specified group.
     *
     * @param groupId The UUID of the group to check
     * @param role The role to check for
     * @return true if the user has the specified role, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean hasGroupRole(UUID groupId, String role) {
        if (groupId == null || role == null) {
            return false;
        }
        
        UUID userId = getCurrentUserId();
        return userGroupRepository.findById(new UserGroupId(userId, groupId))
                .map(userGroup -> role.equals(userGroup.getRole()))
                .orElse(false);
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return UUID.fromString(authentication.getName());
    }
}