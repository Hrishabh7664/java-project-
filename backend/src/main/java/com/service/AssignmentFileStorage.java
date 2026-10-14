package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class AssignmentFileStorage {
    private static final long MAX_BYTES = 10L * 1024 * 1024;
    private final Path root;
    public AssignmentFileStorage(@Value("${app.assignment.storage-dir:./uploads/assignments}") String directory) {
        root = Path.of(directory).toAbsolutePath().normalize();
    }
    public StoredFile store(MultipartFile file) {
        validate(file);
        String original = safeName(file);
        try {
            byte[] bytes = file.getBytes();
            Files.createDirectories(root);
            String key = UUID.randomUUID().toString();
            Path target = resolve(key);
            Files.write(target, bytes, StandardOpenOption.CREATE_NEW);
            return new StoredFile(key, original, bytes.length, target);
        } catch (IOException ex) {
            throw new IllegalStateException("The PDF could not be stored.", ex);
        }
    }
    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Select a non-empty PDF file.");
        if (file.getSize() > MAX_BYTES) throw new IllegalArgumentException("PDF files must be 10 MB or smaller.");
        String original = safeName(file);
        if (!original.toLowerCase().endsWith(".pdf")) throw new IllegalArgumentException("Only PDF submissions are accepted.");
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F' || bytes[4] != '-')
                throw new IllegalArgumentException("The uploaded file does not contain a valid PDF signature.");
        } catch (IOException ex) {
            throw new IllegalArgumentException("The PDF could not be read.", ex);
        }
    }
    private String safeName(MultipartFile file) {
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().replace('\\', '/');
        return original.substring(original.lastIndexOf('/') + 1).replaceAll("[\\r\\n\\p{Cntrl}]", "_");
    }
    public Path resolve(String key) {
        UUID.fromString(key);
        Path target = root.resolve(key + ".pdf").normalize();
        if (!target.startsWith(root)) throw new IllegalArgumentException("Invalid stored file key.");
        return target;
    }
    public void delete(String key) {
        try { Files.deleteIfExists(resolve(key)); } catch (IOException ignored) { }
    }
    public record StoredFile(String key, String originalFilename, long size, Path path) {}
}
