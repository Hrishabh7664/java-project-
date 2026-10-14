package com.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name = "submission_reviews")
public class SubmissionReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "submission_id", nullable = false) private AssignmentSubmission submission;
    @Column(name = "reviewer_email", nullable = false) private String reviewerEmail;
    @Column(nullable = false, length = 40) private String action;
    @Column(precision = 8, scale = 2) private BigDecimal marks;
    @Column(columnDefinition = "TEXT") private String feedback;
    @Column(name = "reviewed_at", nullable = false, updatable = false) private Instant reviewedAt = Instant.now();
    protected SubmissionReview() {}
    public SubmissionReview(AssignmentSubmission submission, String reviewer, String action, BigDecimal marks, String feedback) {
        this.submission = submission; reviewerEmail = reviewer; this.action = action; this.marks = marks; this.feedback = feedback;
    }
}
