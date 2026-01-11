package com.lbgconnect.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalStorageService {

    private final Path rootLocation;
    private final Set<String> allowedExtensions = Set.of(".png", ".jpg", ".jpeg", ".webp");

    public LocalStorageService(@Value("${app.storage.upload-dir}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException ex) {
            throw new StorageException("Could not initialize upload directory", ex);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);
        if (!allowedExtensions.contains(extension)) {
            throw new StorageException("Unsupported file type. Use PNG, JPG, or WEBP.");
        }

        String filename = UUID.randomUUID() + extension;
        try {
            Files.copy(file.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new StorageException("Failed to store file", ex);
        }

        return "/uploads/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot).toLowerCase();
    }
}
