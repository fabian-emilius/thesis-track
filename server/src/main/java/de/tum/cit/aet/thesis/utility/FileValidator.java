package de.tum.cit.aet.thesis.utility;

import de.tum.cit.aet.thesis.exception.UploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Utility class for validating file uploads.
 * Provides centralized file validation logic for the application.
 */
public final class FileValidator {
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/svg+xml"
    );

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_LOGO_SIZE = 2 * 1024 * 1024L; // 2MB
    private static final long MAX_DOCUMENT_SIZE = 20 * 1024 * 1024L; // 20MB
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
    private static final int MAX_FILENAME_LENGTH = 255;

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
        validateFilePresence(file);
        validateFileType(file, ALLOWED_IMAGE_TYPES, "Invalid file type. Allowed types: JPEG, PNG, GIF, SVG");
        validateFileSize(file, MAX_LOGO_SIZE, "File size exceeds maximum allowed size (2MB)");
        validateFilename(file.getOriginalFilename());
    }

    /**
     * Validates a thesis document file.
     *
     * @param file The file to validate
     * @throws UploadException if the file is invalid
     */
    public static void validateThesisDocument(MultipartFile file) {
        validateFilePresence(file);
        validateFileType(file, ALLOWED_DOCUMENT_TYPES, "Invalid file type. Allowed types: PDF, DOC, DOCX");
        validateFileSize(file, MAX_DOCUMENT_SIZE, "File size exceeds maximum allowed size (20MB)");
        validateFilename(file.getOriginalFilename());
    }

    /**
     * Validates a file name for security.
     *
     * @param filename The filename to validate
     * @return true if the filename is safe, false otherwise
     */
    public static boolean isFilenameSafe(String filename) {
        return filename != null &&
               SAFE_FILENAME_PATTERN.matcher(filename).matches() &&
               !filename.contains("..") &&
               filename.length() <= MAX_FILENAME_LENGTH;
    }

    private static void validateFilePresence(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new UploadException("File is required and cannot be empty");
        }
    }

    private static void validateFileType(MultipartFile file, Set<String> allowedTypes, String message) {
        if (!allowedTypes.contains(file.getContentType())) {
            throw new UploadException(message);
        }
    }

    private static void validateFileSize(MultipartFile file, long maxSize, String message) {
        if (file.getSize() > maxSize) {
            throw new UploadException(message);
        }
    }

    private static void validateFilename(String filename) {
        if (!isFilenameSafe(filename)) {
            throw new UploadException("Invalid filename. Use only letters, numbers, dots, underscores, and hyphens");
        }
    }
}
