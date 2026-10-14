package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "academic_classes", uniqueConstraints = @UniqueConstraint(columnNames = {"department_code", "academic_year", "division", "batch"}))
public class AcademicClass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "department_code") private Department department;
    @Column(name = "academic_year", nullable = false) private String academicYear;
    @Column(nullable = false) private String division;
    @Column(nullable = false) private String batch;
    protected AcademicClass() {}
    public AcademicClass(Department department, String academicYear, String division, String batch) {
        this.department = department; this.academicYear = academicYear; this.division = division; this.batch = batch;
    }
    public Long getId() { return id; }
    public Department getDepartment() { return department; }
    public String getAcademicYear() { return academicYear; }
    public String getDivision() { return division; }
    public String getBatch() { return batch; }
}
