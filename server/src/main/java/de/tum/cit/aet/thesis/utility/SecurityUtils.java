package de.tum.cit.aet.thesis.utility;

import org.springframework.web.util.HtmlUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Utility class for security-related operations.
 */
public final class SecurityUtils {
    private SecurityUtils() {
        // Prevent instantiation
    }

    /**
     * Sanitizes user input to prevent XSS attacks.
     *
     * @param input The user input to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * Generates a secure filename for uploaded files.
     *
     * @param originalFilename Original filename
     * @param groupId Group ID
     * @return Secure filename with group context
     */
    public static String generateSecureFilename(String originalFilename, UUID groupId) {
        String extension = getFileExtension(originalFilename);
        return String.format("%s/%s%s", 
            groupId.toString(),
            UUID.randomUUID().toString(),
            extension);
    }

    /**
     * Validates if a file path is within the allowed directory.
     *
     * @param basePath Base directory path
     * @param filePath File path to validate
     * @return true if path is safe, false otherwise
     */
    public static boolean isPathSafe(String basePath, String filePath) {
        try {
            Path normalizedBase = Paths.get(basePath).normalize();
            Path normalizedFile = Paths.get(basePath, filePath).normalize();
            return normalizedFile.startsWith(normalizedBase);
        } catch (Exception e) {
            return false;
        }
    }

    private static String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}
