package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.entity.Group;
import de.tum.cit.aet.thesis.entity.PublishedThesis;
import de.tum.cit.aet.thesis.entity.Topic;
import de.tum.cit.aet.thesis.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for group-based security checks and access control.
 * Provides methods to verify user access to groups, topics, and published theses.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupSecurityService {
    private final GroupService groupService;

    /**
     * Checks if the current user has access to a specific group.
     *
     * @param groupId The UUID of the group to check access for
     * @return true if the user has access, false otherwise
     */
    public boolean hasGroupAccess(UUID groupId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.debug("No authentication found for group access check");
            return false;
        }

        // Admin has access to all groups
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.debug("Admin access granted for group: {}", groupId);
            return true;
        }

        // TODO: Implement group membership check based on user roles/memberships
        log.debug("Regular user access check for group: {}", groupId);
        return false;
    }

    /**
     * Checks if the current user can access a specific topic.
     *
     * @param topic The topic to check access for
     * @return true if the user has access, false otherwise
     */
    public boolean canAccessTopic(Topic topic) {
        if (topic.getGroup() == null) {
            log.debug("Topic has no group restriction, access granted");
            return true;
        }
        
        log.debug("Checking access for topic in group: {}", topic.getGroup().getId());
        return hasGroupAccess(topic.getGroup().getId());
    }

    /**
     * Checks if the current user can access a published thesis.
     *
     * @param publishedThesis The published thesis to check access for
     * @return true if the user has access, false otherwise
     */
    public boolean canAccessPublishedThesis(PublishedThesis publishedThesis) {
        if (publishedThesis.getVisibilityGroups() == null || publishedThesis.getVisibilityGroups().isEmpty()) {
            log.debug("Thesis has no visibility restrictions, access granted");
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.debug("No authentication found for thesis access check");
            return false;
        }

        // Admin has access to all theses
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            log.debug("Admin access granted for thesis");
            return true;
        }

        // Check if user has access to any of the visibility groups
        boolean hasAccess = publishedThesis.getVisibilityGroups().stream()
                .anyMatch(this::hasGroupAccess);
        
        log.debug("User access to thesis: {}", hasAccess);
        return hasAccess;
    }
}