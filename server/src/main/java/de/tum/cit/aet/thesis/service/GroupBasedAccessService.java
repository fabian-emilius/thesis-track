package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.constants.GroupRole;
import de.tum.cit.aet.thesis.entity.ResearchGroup;
import de.tum.cit.aet.thesis.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupBasedAccessService {
    private final GroupMemberService groupMemberService;
    private final AuthenticationService authService;

    @Transactional(readOnly = true)
    public boolean hasGroupAccess(UUID groupId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.hasAnyGroup("admin") || groupMemberService.isMember(groupId, currentUser.getId());
    }

    @Transactional(readOnly = true)
    public boolean hasGroupRole(UUID groupId, GroupRole role) {
        User currentUser = authService.getCurrentUser();
        return currentUser.hasAnyGroup("admin") || groupMemberService.hasRole(groupId, currentUser.getId(), role);
    }

    @Transactional(readOnly = true)
    public boolean canManageGroup(UUID groupId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.hasAnyGroup("admin") || groupMemberService.hasRole(groupId, currentUser.getId(), GroupRole.GROUP_ADMIN);
    }

    @Transactional(readOnly = true)
    public boolean canManageTopics(UUID groupId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.hasAnyGroup("admin") || 
               groupMemberService.hasRole(groupId, currentUser.getId(), GroupRole.GROUP_ADMIN) ||
               groupMemberService.hasRole(groupId, currentUser.getId(), GroupRole.SUPERVISOR);
    }

    @Transactional(readOnly = true)
    public boolean canReviewApplications(UUID groupId) {
        User currentUser = authService.getCurrentUser();
        return currentUser.hasAnyGroup("admin") || 
               groupMemberService.hasRole(groupId, currentUser.getId(), GroupRole.ADVISOR) ||
               groupMemberService.hasRole(groupId, currentUser.getId(), GroupRole.SUPERVISOR);
    }
}