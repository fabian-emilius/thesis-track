package de.tum.cit.aet.thesis.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import de.tum.cit.aet.thesis.constants.UploadFileType;
import de.tum.cit.aet.thesis.exception.UploadException;
import java.util.Map;
import java.util.regex.Pattern;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Set;

@Service
public class UploadService {
    private final Path rootLocation;
    private static final String GROUP_LOGOS_DIR = "group-logos";
    private static final String GROUP_FILES_DIR = "groups";
    private static final int MAX_FILENAME_LENGTH = 255;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB default
    private static final Map<UploadFileType, Set<String>> ALLOWED_EXTENSIONS = Map.of(
        UploadFileType.PDF, Set.of("pdf"),
        UploadFileType.IMAGE, Set.of("jpg", "jpeg", "png", "gif", "webp"),
        UploadFileType.GROUP_LOGO, Set.of("jpg", "jpeg", "png", "gif", "webp")
    );
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[a-zA-Z0-9._-]+");

    @Autowired
    public UploadService(@Value("${thesis-management.storage.upload-location}") String uploadLocation) {
        this.rootLocation = Path.of(uploadLocation);
        initializeDirectories();
    }

    private void initializeDirectories() {
        try {
            Files.createDirectories(rootLocation);
            Files.createDirectories(rootLocation.resolve(GROUP_LOGOS_DIR));
            Files.createDirectories(rootLocation.resolve(GROUP_FILES_DIR));
        } catch (IOException e) {
            throw new UploadException("Failed to create upload directories", e);
        }
    }

    public String store(MultipartFile file, Integer maxSize, UploadFileType type, String groupId) {
        try {
            validateUpload(file, maxSize, type, groupId);
            
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
            
            // TODO: Implement virus scanning integration
            // Consider using ClamAV or similar antivirus solution
            // scanFileForViruses(file);
            
            String sanitizedGroupId = sanitizeFileName(groupId);
            String fileHash = computeFileHash(file);
            String secureFilename = fileHash + "." + extension;

            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);

            if (allowedExtensions != null && !allowedExtensions.contains(extension)) {
                throw new UploadException("File type not allowed");
            }

            String filename = StringUtils.cleanPath(computeFileHash(file) + "." + extension);

            if (filename.contains("..")) {
                throw new UploadException("Cannot store file with relative path outside current directory");
            }

            Path targetPath;
            if (type == UploadFileType.GROUP_LOGO) {
                targetPath = rootLocation.resolve(GROUP_LOGOS_DIR).resolve(groupId + "." + extension);
            } else {
                Path groupPath = rootLocation.resolve(GROUP_FILES_DIR).resolve(groupId);
                Files.createDirectories(groupPath);
                targetPath = groupPath.resolve(filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return targetPath.getFileName().toString();
            }
        }
        catch (IOException | NoSuchAlgorithmException e) {
            throw new UploadException("Failed to store file", e);
        }
    }

    public FileSystemResource load(String filename, String groupId, UploadFileType type) {
        try {
            if (filename.contains("..") || groupId.contains("..")) {
                throw new UploadException("Cannot load file with relative path outside current directory");
            }

            Path filePath;
            if (type == UploadFileType.GROUP_LOGO) {
                filePath = rootLocation.resolve(GROUP_LOGOS_DIR).resolve(groupId + "." + FilenameUtils.getExtension(filename));
            } else {
                filePath = rootLocation.resolve(GROUP_FILES_DIR).resolve(groupId).resolve(filename);
            }

            FileSystemResource file = new FileSystemResource(filePath);

            file.contentLength();

            return file;
        } catch (IOException e) {
            throw new UploadException("Failed to load file", e);
        }
    }

    private void validateUpload(MultipartFile file, Integer maxSize, UploadFileType type, String groupId) {
        if (file == null || file.isEmpty()) {
            throw new UploadException("File is empty or null");
        }

        long effectiveMaxSize = maxSize != null ? maxSize : MAX_FILE_SIZE;
        if (file.getSize() > effectiveMaxSize) {
            throw new UploadException("File size " + file.getSize() + " exceeds maximum allowed size " + effectiveMaxSize);
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.get(type).contains(extension)) {
            throw new UploadException("File type ." + extension + " not allowed for " + type);
        }

        if (file.getOriginalFilename().length() > MAX_FILENAME_LENGTH) {
            throw new UploadException("Filename exceeds maximum length of " + MAX_FILENAME_LENGTH);
        }
    }

    private String sanitizeFileName(String filename) {
        if (filename == null || filename.isEmpty()) {
            throw new UploadException("Filename cannot be null or empty");
        }
        
        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (!SAFE_FILENAME_PATTERN.matcher(sanitized).matches()) {
            throw new UploadException("Invalid filename pattern");
        }
        return sanitized;
    }

    private String computeFileHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            return HexFormat.of().formatHex(digest.digest());
        }
    }
}
