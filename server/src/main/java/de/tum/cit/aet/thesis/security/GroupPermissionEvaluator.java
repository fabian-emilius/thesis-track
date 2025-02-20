package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.UserGroup;
import de.tum.cit.aet.thesis.entity.key.UserGroupId;
import de.tum.cit.aet.thesis.repository.UserGroupRepository;
import de.tum.cit.aet.thesis.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("groupPermissionEvaluator")
public class GroupPermissionEvaluator {
    private final UserGroupRepository userGroupRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public GroupPermissionEvaluator(UserGroupRepository userGroupRepository,
                                   AuthenticationService authenticationService) {
        this.userGroupRepository = userGroupRepository;
        this.authenticationService = authenticationService;
    }

    public boolean hasGroupRole(UUID groupId, String role) {
        UUID userId = authenticationService.getCurrentUser().getId();
        UserGroupId id = new UserGroupId(userId, groupId);
        
        return userGroupRepository.findById(id)
                .map(userGroup -> userGroup.getRole() == GroupRole.valueOf(role))
                .orElse(false);
    }

    public boolean hasAnyGroupRole(UUID groupId, String... roles) {
        UUID userId = authenticationService.getCurrentUser().getId();
        UserGroupId id = new UserGroupId(userId, groupId);
        
        return userGroupRepository.findById(id)
                .map(userGroup -> {
                    for (String role : roles) {
                        if (userGroup.getRole() == GroupRole.valueOf(role)) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    public boolean isMemberOfGroup(UUID groupId) {
        UUID userId = authenticationService.getCurrentUser().getId();
        UserGroupId id = new UserGroupId(userId, groupId);
        return userGroupRepository.existsById(id);
    }
}