package com.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name = "assignment_submissions", uniqueConstraints = @UniqueConstraint(name = "uk_assignment_student_submission", columnNames = {"assignment_id", "student_id"}))
public class AssignmentSubmission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "assignment_id", nullable = false) private Assignment assignment;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "student_id", nullable = false) private Student student;
    @Column(nullable = false, length = 32) private String status = "SUBMITTED";
    @Column(name = "current_version", nullable = false) private int currentVersion = 1;
    @Column(name = "submitted_at", nullable = false) private Instant submittedAt;
    @Column(name = "is_late", nullable = false) private boolean late;
    @Column(precision = 8, scale = 2) private BigDecimal marks;
    @Column(columnDefinition = "TEXT") private String feedback;
    @Column(name = "reviewer_email") private String reviewerEmail;
    @Column(name = "reviewed_at") private Instant reviewedAt;
    protected AssignmentSubmission() {}
    public AssignmentSubmission(Assignment assignment, Student student, Instant submittedAt, boolean late) {
        this.assignment = assignment; this.student = student; this.submittedAt = submittedAt; this.late = late;
    }
    public void setStatus(String value) { status = value; }
    public void setCurrentVersion(int value) { currentVersion = value; }
    public void setSubmittedAt(Instant value) { submittedAt = value; }
    public void setLate(boolean value) { late = value; }
    public void setMarks(BigDecimal value) { marks = value; }
    public void setFeedback(String value) { feedback = value; }
    public void setReviewerEmail(String value) { reviewerEmail = value; }
    public void setReviewedAt(Instant value) { reviewedAt = value; }
    public Long getId() { return id; }
    public Assignment getAssignment() { return assignment; }
    public Student getStudent() { return student; }
    public String getStatus() { return status; }
    public int getCurrentVersion() { return currentVersion; }
    public Instant getSubmittedAt() { return submittedAt; }
    public boolean isLate() { return late; }
    public BigDecimal getMarks() { return marks; }
    public String getFeedback() { return feedback; }
    public String getReviewerEmail() { return reviewerEmail; }
    public Instant getReviewedAt() { return reviewedAt; }
}
