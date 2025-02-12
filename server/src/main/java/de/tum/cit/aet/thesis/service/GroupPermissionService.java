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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupPermissionService {
    private final GroupMemberRepository groupMemberRepository;
    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public void validateGroupMember(UUID groupId) {
        if (!isGroupMember(groupId)) {
            throw new AccessDeniedException("User is not a member of this group");
        }
    }

    @Transactional(readOnly = true)
    public void validateGroupAdmin(UUID groupId) {
        if (!isGroupAdmin(groupId)) {
            throw new AccessDeniedException("User is not an admin of this group");
        }
    }

    @Transactional(readOnly = true)
    public void validateSupervisorOrAdmin(UUID groupId) {
        if (!isSupervisorOrAdmin(groupId)) {
            throw new AccessDeniedException("User must be a supervisor or admin");
        }
    }

    @Transactional(readOnly = true)
    public boolean isGroupMember(UUID groupId) {
        if (authenticationService.isAdmin()) {
            return true;
        }
        return getCurrentUserGroupMember(groupId).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isGroupAdmin(UUID groupId) {
        if (authenticationService.isAdmin()) {
            return true;
        }
        return getCurrentUserGroupMember(groupId)
                .map(member -> member.getRole() == GroupRole.GROUP_ADMIN)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public boolean isSupervisorOrAdmin(UUID groupId) {
        if (authenticationService.isAdmin()) {
            return true;
        }
        return getCurrentUserGroupMember(groupId)
                .map(member -> member.getRole() == GroupRole.GROUP_ADMIN || 
                               member.getRole() == GroupRole.SUPERVISOR)
                .orElse(false);
    }

    private Optional<GroupMember> getCurrentUserGroupMember(UUID groupId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = authenticationService.getCurrentUserId();
        return groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
    }
}
