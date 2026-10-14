package com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String subject;
    private String facultyName;
    private String dueDate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject academicSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_class_id")
    private AcademicClass targetAcademicClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_email")
    private FacultyProfile faculty;

    @Column(name = "deadline_at")
    private LocalDateTime deadlineAt;

    @Column(name = "maximum_marks", precision = 8, scale = 2)
    private BigDecimal maximumMarks;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "allow_resubmission", nullable = false)
    private boolean allowResubmission = true;

    @Column(name = "allow_late_submission", nullable = false)
    private boolean allowLateSubmission;

    public Assignment() {
    }

    public Assignment(String title, String description, String subject, String facultyName, String dueDate, String status) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.facultyName = facultyName;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Subject getAcademicSubject() { return academicSubject; }
    public void setAcademicSubject(Subject academicSubject) { this.academicSubject = academicSubject; }
    public AcademicClass getTargetAcademicClass() { return targetAcademicClass; }
    public void setTargetAcademicClass(AcademicClass targetAcademicClass) { this.targetAcademicClass = targetAcademicClass; }
    public FacultyProfile getFaculty() { return faculty; }
    public void setFaculty(FacultyProfile faculty) { this.faculty = faculty; }
    public LocalDateTime getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(LocalDateTime deadlineAt) { this.deadlineAt = deadlineAt; }
    public BigDecimal getMaximumMarks() { return maximumMarks; }
    public void setMaximumMarks(BigDecimal maximumMarks) { this.maximumMarks = maximumMarks; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public boolean isAllowResubmission() { return allowResubmission; }
    public void setAllowResubmission(boolean allowResubmission) { this.allowResubmission = allowResubmission; }
    public boolean isAllowLateSubmission() { return allowLateSubmission; }
    public void setAllowLateSubmission(boolean allowLateSubmission) { this.allowLateSubmission = allowLateSubmission; }
}
