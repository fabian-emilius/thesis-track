package de.tum.cit.aet.thesis.security;

import de.tum.cit.aet.thesis.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
public class GroupSecurityConfig {
    private final GroupService groupService;

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> groupAuthorizationManager() {
        return (Supplier<Authentication> authentication, RequestAuthorizationContext context) -> {
            if (authentication == null || !authentication.get().isAuthenticated()) {
                return new AuthorizationDecision(false);
            }

            Jwt jwt = (Jwt) authentication.get().getPrincipal();
            String userId = jwt.getSubject();
            
            // Admin has access to all groups
            if (jwt.getClaimAsStringList("roles").contains("admin")) {
                return new AuthorizationDecision(true);
            }

            // Extract groupId from path variables
            String groupId = context.getVariables().get("groupId");
            if (groupId == null) {
                return new AuthorizationDecision(false);
            }

            // Check if user is member of the group
            boolean isMember = groupService.isUserInGroup(
                UUID.fromString(userId),
                UUID.fromString(groupId)
            );

            return new AuthorizationDecision(isMember);
        };
    }
}
