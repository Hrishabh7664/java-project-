package com.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity
@Table(name = "submission_versions", uniqueConstraints = @UniqueConstraint(name = "uk_submission_version", columnNames = {"submission_id", "version_number"}))
public class SubmissionVersion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "submission_id", nullable = false) private AssignmentSubmission submission;
    @Column(name = "version_number", nullable = false) private int versionNumber;
    @Column(name = "file_key", nullable = false, unique = true, length = 36) private String fileKey;
    @Column(name = "original_filename", nullable = false) private String originalFilename;
    @Column(name = "file_size", nullable = false) private long fileSize;
    @Column(name = "uploaded_at", nullable = false, updatable = false) private Instant uploadedAt = Instant.now();
    protected SubmissionVersion() {}
    public SubmissionVersion(AssignmentSubmission submission, int number, String key, String name, long size) {
        this.submission = submission; versionNumber = number; fileKey = key; originalFilename = name; fileSize = size;
    }
    public AssignmentSubmission getSubmission() { return submission; }
    public int getVersionNumber() { return versionNumber; }
    public String getFileKey() { return fileKey; }
    public String getOriginalFilename() { return originalFilename; }
    public long getFileSize() { return fileSize; }
    public Instant getUploadedAt() { return uploadedAt; }
}
