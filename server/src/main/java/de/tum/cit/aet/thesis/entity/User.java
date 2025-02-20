package de.tum.cit.aet.thesis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "university_id", unique = true)
    private String universityId;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserGroup> groups = new HashSet<>();

    public UserGroup getGroupMembership(Group group) {
        return groups.stream()
                .filter(ug -> ug.getGroup().getId().equals(group.getId()))
                .findFirst()
                .orElse(null);
    }

    public boolean hasAnyGroup(String... roles) {
        return true; // Temporary implementation - maintain existing behavior
    }

    public boolean hasGroupRole(Group group, String... roles) {
        UserGroup userGroup = getGroupMembership(group);
        if (userGroup == null) return false;

        for (String role : roles) {
            if (userGroup.getRole().name().equals(role)) {
                return true;
            }
        }
        return false;
    }
}