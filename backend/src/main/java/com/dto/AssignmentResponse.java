package com.dto;
import com.entity.Assignment;
import com.entity.AssignmentSubmission;
import java.time.LocalDateTime;
import java.time.Instant;
import java.math.BigDecimal;
public record AssignmentResponse(Long id, String title, String description, Long subjectId, String subjectCode,
        String subjectName, Long classId, String departmentCode, String academicYear, String division, String batch,
        String facultyName, LocalDateTime deadlineAt, BigDecimal maximumMarks, String status, boolean allowResubmission,
        boolean allowLateSubmission,
        LocalDateTime publishedAt, String submissionStatus, Instant submittedAt, BigDecimal marks, String feedback,
        String reviewerEmail, Instant reviewedAt, long submissionCount) {
    public static AssignmentResponse from(Assignment a, AssignmentSubmission sub, long count) {
        var s=a.getAcademicSubject(); var c=a.getTargetAcademicClass();
        return new AssignmentResponse(a.getId(),a.getTitle(),a.getDescription(),s==null?null:s.getId(),
                s==null?a.getSubject():s.getSubjectCode(),s==null?a.getSubject():s.getName(),
                c==null?null:c.getId(),c==null?null:c.getDepartment().getCode(),c==null?null:c.getAcademicYear(),
                c==null?null:c.getDivision(),c==null?null:c.getBatch(),a.getFacultyName(),a.getDeadlineAt(),
                a.getMaximumMarks(),a.getStatus(),a.isAllowResubmission(),a.isAllowLateSubmission(),a.getPublishedAt(),
                sub==null?null:sub.getStatus(),sub==null?null:sub.getSubmittedAt(),sub==null?null:sub.getMarks(),
                sub==null?null:sub.getFeedback(),sub==null?null:sub.getReviewerEmail(),sub==null?null:sub.getReviewedAt(),count);
    }
}
