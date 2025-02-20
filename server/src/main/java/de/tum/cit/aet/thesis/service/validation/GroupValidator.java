package de.tum.cit.aet.thesis.service.validation;

import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import org.springframework.stereotype.Component;

/**
 * Validator component for group-related data.
 * Centralizes validation logic for group operations.
 */
@Component
public class GroupValidator {

    /**
     * Validates group slug format.
     * Slugs must be lowercase, contain only letters, numbers, and hyphens.
     *
     * @param slug The slug to validate
     * @throws ResourceInvalidParametersException if the slug is invalid
     */
    public void validateSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new ResourceInvalidParametersException("Slug cannot be empty");
        }
        if (!slug.matches("^[a-z0-9-]+$")) {
            throw new ResourceInvalidParametersException("Slug can only contain lowercase letters, numbers, and hyphens");
        }
    }

    /**
     * Validates group name format.
     * Names must not be empty and must be within length limits.
     *
     * @param name The name to validate
     * @throws ResourceInvalidParametersException if the name is invalid
     */
    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResourceInvalidParametersException("Name cannot be empty");
        }
        if (name.length() > 255) {
            throw new ResourceInvalidParametersException("Name cannot exceed 255 characters");
        }
    }

    /**
     * Validates group member role.
     * Roles must be one of the predefined valid roles.
     *
     * @param role The role to validate
     * @throws ResourceInvalidParametersException if the role is invalid
     */
    public void validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new ResourceInvalidParametersException("Role cannot be empty");
        }
        if (!isValidRole(role)) {
            throw new ResourceInvalidParametersException("Invalid role: " + role);
        }
    }

    private boolean isValidRole(String role) {
        return role.matches("^(supervisor|advisor|administrator)$");
    }
}
