package com.dto;
import com.entity.AssignmentSubmission;
import com.entity.SubmissionVersion;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public final class SubmissionDtos {
    private SubmissionDtos() {}
    public record ReviewRequest(String action, BigDecimal marks, String feedback) {}
    public record VersionInfo(int version, String filename, long size, Instant uploadedAt, String fileUrl) {}
    public record SubmissionResponse(Long id, Long assignmentId, String assignmentTitle, String studentName,
        String studentEmail, String status, int currentVersion, Instant submittedAt, boolean late,
        BigDecimal marks, BigDecimal maximumMarks, String feedback, String reviewerEmail, Instant reviewedAt,
        List<VersionInfo> versions) {
        public static SubmissionResponse from(AssignmentSubmission s, List<SubmissionVersion> versions) {
            return new SubmissionResponse(s.getId(), s.getAssignment().getId(), s.getAssignment().getTitle(),
                s.getStudent().getName(),s.getStudent().getEmail(),s.getStatus(),s.getCurrentVersion(),s.getSubmittedAt(),
                s.isLate(),s.getMarks(),s.getAssignment().getMaximumMarks(),s.getFeedback(),s.getReviewerEmail(),
                s.getReviewedAt(),versions.stream().map(v->new VersionInfo(v.getVersionNumber(),v.getOriginalFilename(),
                    v.getFileSize(),v.getUploadedAt(),"/api/submissions/"+s.getId()+"/versions/"+v.getVersionNumber()+"/file")).toList());
        }
    }
}
