package com.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "attendance_sessions", uniqueConstraints = @UniqueConstraint(name = "uk_attendance_assignment_date", columnNames = {"assignment_id", "held_on"}))
public class AttendanceSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "assignment_id", nullable = false) private TeachingAssignment assignment;
    @Column(name = "held_on", nullable = false) private LocalDate heldOn;
    @Column(name = "created_by", nullable = false) private String createdBy;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false) private Instant updatedAt = Instant.now();
    protected AttendanceSession() {}
    public AttendanceSession(TeachingAssignment assignment, LocalDate heldOn, String createdBy) {
        this.assignment = assignment; this.heldOn = heldOn; this.createdBy = createdBy;
    }
    public void touch() { updatedAt = Instant.now(); }
    public Long getId() { return id; }
    public TeachingAssignment getAssignment() { return assignment; }
    public LocalDate getHeldOn() { return heldOn; }
    public String getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
