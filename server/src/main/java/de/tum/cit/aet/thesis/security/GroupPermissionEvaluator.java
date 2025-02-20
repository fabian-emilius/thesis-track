package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GroupPermissionEvaluator implements PermissionEvaluator {
    private final GroupService groupService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID userId = UUID.fromString(jwt.getSubject());

        if (targetDomainObject instanceof Group) {
            return hasGroupPermission(userId, (Group) targetDomainObject, (String) permission);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || !(permission instanceof String)) {
            return false;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        UUID userId = UUID.fromString(jwt.getSubject());

        if ("Group".equals(targetType)) {
            Group group = groupService.getGroupById((UUID) targetId);
            return hasGroupPermission(userId, group, (String) permission);
        }

        return false;
    }

    private boolean hasGroupPermission(UUID userId, Group group, String permission) {
        String userRole = groupService.getUserGroupRole(group.getId(), userId);
        if (userRole == null) {
            return false;
        }

        return switch (permission) {
            case "ADMIN" -> userRole.equals("ADMIN");
            case "SUPERVISOR" -> userRole.equals("ADMIN") || userRole.equals("SUPERVISOR");
            case "ADVISOR" -> userRole.equals("ADMIN") || userRole.equals("SUPERVISOR") || userRole.equals("ADVISOR");
            case "MEMBER" -> true; // Any role grants basic membership
            default -> false;
        };
    }
}