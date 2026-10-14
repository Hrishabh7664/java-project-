package com.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_enrollments", uniqueConstraints = @UniqueConstraint(name = "uk_student_class_enrollment", columnNames = {"student_id", "class_id"}))
public class StudentEnrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id", nullable = false) private Student student;
    @ManyToOne(optional = false) @JoinColumn(name = "class_id", nullable = false) private AcademicClass academicClass;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "enrolled_on", nullable = false, updatable = false) private LocalDate enrolledOn = LocalDate.now();
    protected StudentEnrollment() {}
    public StudentEnrollment(Student student, AcademicClass academicClass) { this.student = student; this.academicClass = academicClass; }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public AcademicClass getAcademicClass() { return academicClass; }
    public boolean isActive() { return active; }
}
