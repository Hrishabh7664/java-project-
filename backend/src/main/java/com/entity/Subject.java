package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects", uniqueConstraints = @UniqueConstraint(columnNames = {"subject_code", "academic_year"}))
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "department_code") private Department department;
    @Column(name = "academic_year", nullable = false) private String academicYear;
    @Column(nullable = false) private Integer semester;
    @Column(name = "subject_code", nullable = false) private String subjectCode;
    @Column(nullable = false) private String name;
    @Column(columnDefinition = "TEXT") private String syllabus;
    protected Subject() {}
    public Subject(Department department, String academicYear, Integer semester, String subjectCode, String name) {
        this.department = department; this.academicYear = academicYear; this.semester = semester; this.subjectCode = subjectCode; this.name = name;
    }
    public Long getId() { return id; }
    public Department getDepartment() { return department; }
    public String getAcademicYear() { return academicYear; }
    public Integer getSemester() { return semester; }
    public String getSubjectCode() { return subjectCode; }
    public String getName() { return name; }
    public String getSyllabus() { return syllabus; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }
}
