package com.service;

import com.dto.AuthRequest;
import com.dto.AuthResponse;
import com.dto.RegisterRequest;
import com.entity.Student;
import com.repository.StudentRepository;
import com.repository.AcademicClassRepository;
import com.repository.StudentEnrollmentRepository;
import com.entity.AcademicClass;
import com.entity.StudentEnrollment;
import com.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class AuthService {
    private final StudentRepository students;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AcademicClassRepository academicClasses;
    private final StudentEnrollmentRepository enrollments;

    public AuthService(StudentRepository students, PasswordEncoder passwordEncoder, JwtService jwtService,
                       AuthenticationManager authenticationManager, AcademicClassRepository academicClasses,
                       StudentEnrollmentRepository enrollments) {
        this.students = students;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.academicClasses = academicClasses;
        this.enrollments = enrollments;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (!email.endsWith("@tsdcem.ac.in")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use your @tsdcem.ac.in college email");
        }
        if (students.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        Student student = new Student();
        student.setName(request.name().trim());
        student.setEmail(email);
        student.setPasswordHash(passwordEncoder.encode(request.password()));
        student.setRole("STUDENT");
        student.setDepartment(request.department());
        student.setYear(request.year());
        student.setDivision(request.division());
        student.setBatch(request.batch());
        Student saved = students.save(student);
        if (request.department() != null && request.year() != null && request.division() != null && request.batch() != null) {
            AcademicClass academicClass = academicClasses.findByDepartmentCodeAndAcademicYearAndDivisionAndBatch(
                    request.department().trim(), request.year().trim(), request.division().trim(), request.batch().trim()).orElse(null);
            if (academicClass != null) enrollments.save(new StudentEnrollment(saved, academicClass));
        }
        return response(saved);
    }

    public AuthResponse login(AuthRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        Student student = students.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return response(student);
    }

    private AuthResponse response(Student student) {
        return new AuthResponse(jwtService.issue(student), "Bearer", jwtService.getExpirationMillis(),
                new AuthResponse.UserSummary(student.getId(), student.getName(), student.getEmail(),
                        student.getRole(), student.getDepartment(), student.getYear(), student.getDivision()));
    }
}
