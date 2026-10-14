package com.controller;

import com.dto.SyllabusRequest;
import com.dto.ScheduleRequest;
import com.dto.TimetableEntry;
import com.repository.StudentEnrollmentRepository;
import com.repository.SubjectRepository;
import com.repository.TeachingAssignmentRepository;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalTime;

@RestController
public class TimetableController {
    private final TeachingAssignmentRepository teaching;
    private final StudentEnrollmentRepository enrollments;
    private final SubjectRepository subjects;
    public TimetableController(TeachingAssignmentRepository teaching,StudentEnrollmentRepository enrollments,
            SubjectRepository subjects) {
        this.teaching=teaching;this.enrollments=enrollments;this.subjects=subjects;
    }
    @GetMapping("/api/faculty/timetable")
    @Transactional(readOnly = true)
    public List<TimetableEntry> faculty(Authentication auth) {
        return teaching.findByFacultyEmailOrderByDayOfWeekAscStartTimeAsc(auth.getName()).stream().map(TimetableEntry::from).toList();
    }
    @GetMapping("/api/student/timetable")
    @Transactional(readOnly = true)
    public List<TimetableEntry> student(Authentication auth) {
        List<Long> classIds=enrollments.findByStudentEmailIgnoreCaseAndActiveTrue(auth.getName()).stream()
                .map(e->e.getAcademicClass().getId()).distinct().toList();
        if(classIds.isEmpty()) return List.of();
        return teaching.findByAcademicClassIdInOrderByDayOfWeekAscStartTimeAsc(classIds).stream().map(TimetableEntry::from).toList();
    }
    @PutMapping("/api/faculty/subjects/{subjectId}/syllabus")
    @Transactional
    public TimetableEntry updateSyllabus(@PathVariable Long subjectId,@Valid @RequestBody SyllabusRequest request,Authentication auth) {
        var subject=subjects.findById(subjectId).orElseThrow(()->new IllegalArgumentException("Subject not found."));
        if(!teaching.existsByFacultyEmailAndSubjectId(auth.getName(),subjectId))
            throw new AccessDeniedException("You are not assigned to this subject.");
        subject.setSyllabus(request.syllabus());
        subjects.save(subject);
        return teaching.findByFacultyEmailOrderByDayOfWeekAscStartTimeAsc(auth.getName()).stream()
                .filter(a->a.getSubject().getId().equals(subjectId)).findFirst().map(TimetableEntry::from).orElseThrow();
    }
    @PutMapping("/api/faculty/timetable/{assignmentId}")
    @Transactional
    public TimetableEntry updateSchedule(@PathVariable Long assignmentId,@Valid @RequestBody ScheduleRequest request,Authentication auth) {
        var assignment=teaching.findByIdAndFacultyEmail(assignmentId,auth.getName())
                .orElseThrow(()->new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        if(!LocalTime.parse(request.endTime()).isAfter(LocalTime.parse(request.startTime())))
            throw new IllegalArgumentException("Lecture end time must be after its start time.");
        assignment.setDayOfWeek(request.dayOfWeek()); assignment.setStartTime(request.startTime());
        assignment.setEndTime(request.endTime()); assignment.setClassroom(request.classroom().trim());
        return TimetableEntry.from(teaching.save(assignment));
    }
}
