package com.dto;

import com.entity.EventRegistration;
import java.time.Instant;

public record EventRegistrantResponse(
        Long registrationId,
        Long studentId,
        String name,
        String email,
        String department,
        String year,
        String division,
        Instant registeredAt) {

    public static EventRegistrantResponse from(EventRegistration registration) {
        var student = registration.getStudent();
        return new EventRegistrantResponse(registration.getId(), student.getId(), student.getName(),
                student.getEmail(), student.getDepartment(), student.getYear(), student.getDivision(),
                registration.getRegisteredAt());
    }
}
