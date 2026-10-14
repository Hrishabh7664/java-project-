package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_records", uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "student_id"}))
public class AttendanceRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "session_id", nullable = false) private AttendanceSession session;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id", nullable = false) private Student student;
    @Column(nullable = false) private boolean present;
    protected AttendanceRecord() {}
    public AttendanceRecord(AttendanceSession session, Student student, boolean present) { this.session = session; this.student = student; this.present = present; }
    public void setPresent(boolean present) { this.present = present; }
    public Long getId() { return id; }
    public AttendanceSession getSession() { return session; }
    public Student getStudent() { return student; }
    public boolean isPresent() { return present; }
}
