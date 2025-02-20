package de.tum.cit.aet.thesis.service;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.User;
import de.tum.cit.aet.thesis.exception.request.AccessDeniedException;
import de.tum.cit.aet.thesis.exception.request.ResourceNotFoundException;
import de.tum.cit.aet.thesis.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupContextService {
    private final GroupRepository groupRepository;
    private final UserService userService;
    private final AccessManagementService accessManagementService;

    public Group validateGroupAccess(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        User currentUser = userService.getCurrentUser();
        if (!currentUser.hasAnyGroup("admin") && !accessManagementService.isGroupMember(groupId)) {
            throw new AccessDeniedException("No access to this group");
        }

        return group;
    }

    public void validateGroupAdminAccess(UUID groupId) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.hasAnyGroup("admin") && !accessManagementService.isGroupAdmin(groupId)) {
            throw new AccessDeniedException("Admin access required");
        }
    }

    public void validateGroupSupervisorAccess(UUID groupId) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.hasAnyGroup("admin") && 
            !accessManagementService.isGroupAdmin(groupId) && 
            !accessManagementService.isGroupSupervisor(groupId)) {
            throw new AccessDeniedException("Supervisor access required");
        }
    }

    public void validateGroupAdvisorAccess(UUID groupId) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.hasAnyGroup("admin") && 
            !accessManagementService.isGroupAdmin(groupId) && 
            !accessManagementService.isGroupSupervisor(groupId) &&
            !accessManagementService.isGroupAdvisor(groupId)) {
            throw new AccessDeniedException("Advisor access required");
        }
    }
}