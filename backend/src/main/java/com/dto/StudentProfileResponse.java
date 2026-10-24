package com.dto;

import com.entity.Student;

public record StudentProfileResponse(
        Long id,
        String name,
        String email,
        String role,
        String department,
        String year,
        String division,
        String batch,
        String bio
) {
    public static StudentProfileResponse from(Student student) {
        return new StudentProfileResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getRole(),
                student.getDepartment(),
                student.getYear(),
                student.getDivision(),
                student.getBatch(),
                student.getBio()
        );
    }
}
