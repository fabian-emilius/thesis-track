package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("groupSecurity")
@RequiredArgsConstructor
public class GroupSecurityEvaluator {
    private final UserGroupRepository userGroupRepository;

    public boolean hasGroupAccess(Authentication authentication, UUID groupId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String userId = authentication.getName();
        return userGroupRepository.findByUserIdAndGroupId(
                UUID.fromString(userId), groupId) != null;
    }

    public boolean isGroupAdmin(Authentication authentication, UUID groupId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String userId = authentication.getName();
        var userGroup = userGroupRepository.findByUserIdAndGroupId(
                UUID.fromString(userId), groupId);
        return userGroup != null && "ADMIN".equals(userGroup.getRole());
    }
}