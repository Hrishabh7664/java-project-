package com.service;

import com.dto.AssignmentRequest;
import com.dto.AssignmentResponse;
import com.entity.*;
import com.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class AssignmentService {
    private static final ZoneId COLLEGE_ZONE = ZoneId.of("Asia/Kolkata");
    private final AssignmentRepository assignments;
    private final AssignmentSubmissionRepository submissions;
    private final SubjectRepository subjects;
    private final AcademicClassRepository classes;
    private final FacultyProfileRepository faculty;
    private final TeachingAssignmentRepository teaching;
    private final StudentEnrollmentRepository enrollments;
    private final StudentRepository students;
    public AssignmentService(AssignmentRepository assignments, AssignmentSubmissionRepository submissions,
            SubjectRepository subjects, AcademicClassRepository classes, FacultyProfileRepository faculty,
            TeachingAssignmentRepository teaching, StudentEnrollmentRepository enrollments, StudentRepository students) {
        this.assignments=assignments; this.submissions=submissions; this.subjects=subjects; this.classes=classes;
        this.faculty=faculty; this.teaching=teaching; this.enrollments=enrollments; this.students=students;
    }

    @Transactional(readOnly=true)
    public List<AssignmentResponse> getAll(Authentication auth) {
        String role = role(auth);
        List<Assignment> rows;
        if ("STUDENT".equals(role)) {
            List<Long> classIds = enrollments.findByStudentEmailIgnoreCaseAndActiveTrue(auth.getName()).stream()
                    .map(e->e.getAcademicClass().getId()).distinct().toList();
            if (classIds.isEmpty()) return List.of();
            rows = assignments.findByTargetAcademicClassIdInAndStatusOrderByDeadlineAtAsc(classIds,"PUBLISHED");
        } else if ("FACULTY".equals(role)) {
            rows = assignments.findByFacultyEmailOrderByDeadlineAtAsc(auth.getName());
        } else {
            rows = assignments.findAll();
        }
        if (rows.isEmpty()) return List.of();
        Map<Long,Long> counts=new HashMap<>();
        for (Object[] row : submissions.countGroupedByAssignmentIds(rows.stream().map(Assignment::getId).toList()))
            counts.put((Long)row[0],(Long)row[1]);
        return rows.stream().map(a->AssignmentResponse.from(a,"STUDENT".equals(role)?submission(a,auth):null,counts.getOrDefault(a.getId(),0L))).toList();
    }

    @Transactional(readOnly=true)
    public AssignmentResponse get(Long id, Authentication auth) {
        Assignment a = assignments.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        String role=role(auth);
        if ("STUDENT".equals(role)) {
            boolean enrolled = enrollments.findByStudentEmailIgnoreCaseAndActiveTrue(auth.getName()).stream()
                    .anyMatch(e->e.getAcademicClass().getId().equals(a.getTargetAcademicClass()==null?null:a.getTargetAcademicClass().getId()));
            if (!enrolled || !"PUBLISHED".equals(a.getStatus())) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return toResponse(a,submission(a,auth));
        }
        if ("FACULTY".equals(role) && !auth.getName().equalsIgnoreCase(a.getFaculty()==null?"":a.getFaculty().getEmail()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return toResponse(a,null);
    }

    @Transactional
    public AssignmentResponse create(AssignmentRequest request, Authentication auth) {
        String email=auth.getName();
        FacultyProfile profile=faculty.findById(email).orElseThrow(()->new AccessDeniedException("Faculty profile is not configured."));
        if (!teaching.existsByFacultyEmailAndSubjectIdAndAcademicClassId(email,request.subjectId(),request.classId()))
            throw new AccessDeniedException("You are not assigned to teach this subject and class.");
        if (request.deadlineAt().isBefore(collegeNow())) throw new IllegalArgumentException("The deadline must be in the future.");
        Subject subject=subjects.findById(request.subjectId()).orElseThrow(()->new IllegalArgumentException("Subject not found."));
        AcademicClass academicClass=classes.findById(request.classId()).orElseThrow(()->new IllegalArgumentException("Class not found."));
        Assignment a=new Assignment();
        apply(a,request,subject,academicClass,profile);
        return toResponse(assignments.save(a),null);
    }

    @Transactional
    public AssignmentResponse update(Long id, AssignmentRequest request, Authentication auth) {
        Assignment a=assignments.findByIdAndFacultyEmail(id,auth.getName())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!teaching.existsByFacultyEmailAndSubjectIdAndAcademicClassId(auth.getName(),request.subjectId(),request.classId()))
            throw new AccessDeniedException("You are not assigned to teach this subject and class.");
        if (submissions.countByAssignmentId(id)>0) throw new IllegalArgumentException("Assignments with submissions cannot be changed.");
        if (request.deadlineAt().isBefore(collegeNow())) throw new IllegalArgumentException("The deadline must be in the future.");
        Subject subject=subjects.findById(request.subjectId()).orElseThrow(()->new IllegalArgumentException("Subject not found."));
        AcademicClass academicClass=classes.findById(request.classId()).orElseThrow(()->new IllegalArgumentException("Class not found."));
        FacultyProfile profile=faculty.findById(auth.getName()).orElseThrow();
        apply(a,request,subject,academicClass,profile);
        return toResponse(assignments.save(a),null);
    }

    @Transactional
    public void delete(Long id, Authentication auth) {
        Assignment a=assignments.findByIdAndFacultyEmail(id,auth.getName())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (submissions.countByAssignmentId(id)>0) throw new IllegalArgumentException("An assignment with submissions cannot be deleted.");
        assignments.delete(a);
    }

    private void apply(Assignment a,AssignmentRequest r,Subject s,AcademicClass c,FacultyProfile f) {
        a.setTitle(r.title().trim()); a.setDescription(r.description().trim());
        a.setSubject(s.getSubjectCode()+" — "+s.getName()); a.setAcademicSubject(s); a.setTargetAcademicClass(c);
        a.setFaculty(f); a.setFacultyName(f.getName()); a.setDeadlineAt(r.deadlineAt()); a.setDueDate(r.deadlineAt().toString());
        a.setMaximumMarks(r.maximumMarks()); a.setStatus(r.status());
        a.setAllowResubmission(r.allowResubmission()==null || r.allowResubmission());
        a.setAllowLateSubmission(Boolean.TRUE.equals(r.allowLateSubmission()));
        if ("PUBLISHED".equals(r.status()) && a.getPublishedAt()==null) a.setPublishedAt(collegeNow());
    }

    private AssignmentResponse toResponse(Assignment a,AssignmentSubmission sub) {
        return AssignmentResponse.from(a,sub,submissions.countByAssignmentId(a.getId()));
    }
    private AssignmentSubmission submission(Assignment a,Authentication auth) {
        Student student=students.findByEmailIgnoreCase(auth.getName()).orElseThrow();
        return submissions.findByAssignmentIdAndStudentId(a.getId(),student.getId()).orElse(null);
    }
    private String role(Authentication auth) {
        return auth.getAuthorities().stream().findFirst().map(a->a.getAuthority().replace("ROLE_","")).orElse("");
    }
    public static LocalDateTime collegeNow() { return LocalDateTime.now(COLLEGE_ZONE); }
}
