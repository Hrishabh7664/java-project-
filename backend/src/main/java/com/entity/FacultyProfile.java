package com.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "faculty_profiles")
public class FacultyProfile {
    @Id @Column(length = 255) private String email;
    @Column(nullable = false) private String name;
    @ManyToOne(optional = false) @JoinColumn(name = "department_code") private Department department;
    protected FacultyProfile() {}
    public FacultyProfile(String email, String name, Department department) { this.email = email; this.name = name; this.department = department; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public Department getDepartment() { return department; }
}
