package de.tum.cit.aet.thesis.utility;

import de.tum.cit.aet.thesis.exception.request.ResourceInvalidParametersException;
import org.springframework.util.StringUtils;

/**
 * Utility class for validating request parameters.
 * Provides centralized validation logic for common parameters.
 */
public class RequestValidator {
    private static final int MAX_SEARCH_QUERY_LENGTH = 255;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    /**
     * Validates a search query parameter.
     *
     * @param searchQuery The search query to validate
     * @return The validated and sanitized search query
     * @throws ResourceInvalidParametersException if the query is invalid
     */
    public static String validateSearchQuery(String searchQuery) {
        if (searchQuery != null) {
            if (searchQuery.length() > MAX_SEARCH_QUERY_LENGTH) {
                throw new ResourceInvalidParametersException("Search query is too long");
            }
            return sanitizeSearchQuery(searchQuery);
        }
        return null;
    }

    /**
     * Validates a title parameter.
     *
     * @param title The title to validate
     * @throws ResourceInvalidParametersException if the title is invalid
     */
    public static void validateTitle(String title) {
        if (!StringUtils.hasText(title)) {
            throw new ResourceInvalidParametersException("Title is required");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new ResourceInvalidParametersException("Title is too long");
        }
    }

    /**
     * Validates a description parameter.
     *
     * @param description The description to validate
     * @throws ResourceInvalidParametersException if the description is invalid
     */
    public static void validateDescription(String description) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ResourceInvalidParametersException("Description is too long");
        }
    }

    /**
     * Sanitizes a search query to prevent SQL injection and other attacks.
     *
     * @param query The query to sanitize
     * @return The sanitized query
     */
    private static String sanitizeSearchQuery(String query) {
        if (query == null) return null;
        // Remove any SQL injection attempts or special characters
        return query.replaceAll("[%;\\\\/'\"\\[\\]\\{\\}]+", "");
    }
}