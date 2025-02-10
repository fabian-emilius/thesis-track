package de.tum.cit.aet.thesis.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final JwtAuthConfig config;

    public JwtAuthConverter(JwtAuthConfig config) {
        this.config = config;
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    @Nullable
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                Stream.concat(
                    jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                    extractResourceRoles(jwt).stream()
                ),
                extractGroups(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaim("preferred_username"));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Map<String, Collection<String>>> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Set.of();
        }

        var resourceObject = resourceAccess.get(config.getClientId());
        if (resourceObject == null) {
            return Set.of();
        }

        var resourceRoles = resourceObject.get("roles");
        if (resourceRoles == null) {
            return Set.of();
        }

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractGroups(Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList("groups");
        if (groups == null) {
            return Set.of();
        }

        return groups.stream()
                .map(group -> new SimpleGrantedAuthority("GROUP_" + group))
                .collect(Collectors.toSet());
    }
}