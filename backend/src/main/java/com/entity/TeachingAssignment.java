package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teaching_assignments", uniqueConstraints = @UniqueConstraint(columnNames = {"subject_id", "class_id", "lecture_number"}))
public class TeachingAssignment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "subject_id") private Subject subject;
    @ManyToOne(optional = false) @JoinColumn(name = "class_id") private AcademicClass academicClass;
    @ManyToOne(optional = false) @JoinColumn(name = "faculty_email") private FacultyProfile faculty;
    @Column(name = "lecture_number", nullable = false) private Integer lectureNumber;
    @Column(name = "day_of_week", nullable = false) private String dayOfWeek;
    @Column(name = "start_time", nullable = false) private String startTime;
    @Column(name = "end_time", nullable = false) private String endTime;
    protected TeachingAssignment() {}
    public TeachingAssignment(Subject subject, AcademicClass academicClass, FacultyProfile faculty, Integer lectureNumber,
                              String dayOfWeek, String startTime, String endTime) {
        this.subject = subject; this.academicClass = academicClass; this.faculty = faculty; this.lectureNumber = lectureNumber;
        this.dayOfWeek = dayOfWeek; this.startTime = startTime; this.endTime = endTime;
    }
    public Long getId() { return id; }
    public Subject getSubject() { return subject; }
    public AcademicClass getAcademicClass() { return academicClass; }
    public FacultyProfile getFaculty() { return faculty; }
    public Integer getLectureNumber() { return lectureNumber; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
}
