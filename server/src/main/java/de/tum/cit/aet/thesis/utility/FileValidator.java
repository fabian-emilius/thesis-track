package de.tum.cit.aet.thesis.utility;

import de.tum.cit.aet.thesis.exception.UploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Utility class for validating file uploads.
 */
public final class FileValidator {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/svg+xml"
    );

    private static final long MAX_LOGO_SIZE = 2 * 1024 * 1024; // 2MB

    private FileValidator() {
        // Prevent instantiation
    }

    /**
     * Validates a group logo file.
     *
     * @param file The file to validate
     * @throws UploadException if the file is invalid
     */
    public static void validateGroupLogo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadException("Logo file is required");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new UploadException("Invalid file type. Allowed types: JPEG, PNG, GIF, SVG");
        }

        if (file.getSize() > MAX_LOGO_SIZE) {
            throw new UploadException("File size exceeds maximum allowed size (2MB)");
        }
    }

    /**
     * Validates a file name for security.
     *
     * @param filename The filename to validate
     * @return true if the filename is safe, false otherwise
     */
    public static boolean isFilenameSafe(String filename) {
        return filename != null && 
               filename.matches("^[a-zA-Z0-9._-]+$") && 
               !filename.contains("..");
    }
}
