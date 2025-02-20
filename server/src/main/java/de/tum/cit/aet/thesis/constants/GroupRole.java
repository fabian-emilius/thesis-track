package de.tum.cit.aet.thesis.constants;

/**
 * Enumeration of possible group roles in the system.
 * Roles are hierarchical, with ADMIN having the highest privileges.
 */
public enum GroupRole {
    ADMIN("ADMIN"),
    SUPERVISOR("SUPERVISOR"),
    ADVISOR("ADVISOR"),
    MEMBER("MEMBER");

    private final String role;

    GroupRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    /**
     * Checks if this role has the required permission level.
     * @param requiredRole The role level required for the operation
     * @return true if this role has sufficient permissions
     */
    public boolean hasPermission(String requiredRole) {
        return switch (this) {
            case ADMIN -> true; // Admin has all permissions
            case SUPERVISOR -> !requiredRole.equals(ADMIN.role);
            case ADVISOR -> requiredRole.equals(ADVISOR.role) || requiredRole.equals(MEMBER.role);
            case MEMBER -> requiredRole.equals(MEMBER.role);
        };
    }

    /**
     * Validates if the provided role string is a valid group role.
     * @param role Role string to validate
     * @return true if the role is valid
     */
    public static boolean isValid(String role) {
        for (GroupRole groupRole : values()) {
            if (groupRole.role.equals(role)) {
                return true;
            }
        }
        return false;
    }
}