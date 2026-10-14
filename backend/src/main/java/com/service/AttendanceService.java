package com.service;

import com.dto.AttendanceDtos.*;
import com.dto.StudentSession;
import com.entity.*;
import com.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final TeachingAssignmentRepository assignments;
    private final StudentRepository students;
    private final AttendanceSessionRepository sessions;
    private final AttendanceRecordRepository records;
    private final AttendanceChangeRepository changes;
    private final StudentEnrollmentRepository enrollments;

    public AttendanceService(TeachingAssignmentRepository assignments, StudentRepository students,
                             AttendanceSessionRepository sessions, AttendanceRecordRepository records,
                             AttendanceChangeRepository changes, StudentEnrollmentRepository enrollments) {
        this.assignments = assignments; this.students = students; this.sessions = sessions;
        this.records = records; this.changes = changes; this.enrollments = enrollments;
    }

    @Transactional(readOnly = true)
    public List<AssignmentOption> options(String facultyEmail) {
        return assignments.findByFacultyEmailOrderByAcademicClassDepartmentCodeAscAcademicClassAcademicYearAscSubjectSubjectCodeAsc(facultyEmail)
                .stream().map(this::option).toList();
    }

    @Transactional(readOnly = true)
    public RosterResponse roster(Long assignmentId, LocalDate date, String facultyEmail) {
        TeachingAssignment assignment = assigned(assignmentId, facultyEmail);
        validateScheduledDate(assignment, date);
        var roster = rosterFor(assignment);
        var session = sessions.findByAssignmentIdAndHeldOn(assignmentId, date).orElse(null);
        Map<Long, Boolean> status = session == null ? Map.of() : records.findBySessionIdOrderByStudentNameAsc(session.getId()).stream()
                .collect(Collectors.toMap(r -> r.getStudent().getId(), AttendanceRecord::isPresent));
        return new RosterResponse(assignmentId, session == null ? null : session.getId(), date,
                roster.stream().map(s -> rosterStudent(s, status.get(s.getId()))).toList());
    }

    @Transactional
    public SessionResponse submit(SaveRequest request, String facultyEmail) {
        TeachingAssignment assignment = assigned(request.assignmentId(), facultyEmail);
        validateScheduledDate(assignment, request.date());
        if (sessions.findByAssignmentIdAndHeldOn(assignment.getId(), request.date()).isPresent())
            throw new AttendanceConflictException("Attendance has already been recorded for this lecture and date.");
        validateRoster(assignment, request.attendance());
        AttendanceSession session = sessions.save(new AttendanceSession(assignment, request.date(), facultyEmail));
        var saved = request.attendance().stream().map(mark ->
                new AttendanceRecord(session, students.findById(mark.studentId()).orElseThrow(), mark.present())).toList();
        records.saveAll(saved);
        return sessionResponse(session);
    }

    @Transactional
    public SessionResponse correct(Long sessionId, SaveRequest request, String facultyEmail) {
        AttendanceSession session = sessions.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Attendance session not found."));
        TeachingAssignment assignment = assigned(session.getAssignment().getId(), facultyEmail);
        if (!Objects.equals(assignment.getId(), request.assignmentId()) || !session.getHeldOn().equals(request.date()))
            throw new IllegalArgumentException("Correction must match the original class, subject, and date.");
        validateRoster(assignment, request.attendance());
        Map<Long, AttendanceRecord> byStudent = records.findBySessionId(sessionId).stream()
                .collect(Collectors.toMap(r -> r.getStudent().getId(), r -> r));
        for (AttendanceMark mark : request.attendance()) {
            AttendanceRecord record = byStudent.get(mark.studentId());
            if (record == null) throw new IllegalArgumentException("The register does not contain the full class roster.");
            if (record.isPresent() != mark.present()) {
                changes.save(new AttendanceChange(record, facultyEmail, record.isPresent(), mark.present()));
                record.setPresent(mark.present());
            }
        }
        session.touch();
        return sessionResponse(session);
    }

    @Transactional(readOnly = true)
    public List<SessionResponse> history(String facultyEmail) {
        return sessions.findByAssignmentFacultyEmailOrderByHeldOnDesc(facultyEmail).stream().map(this::sessionResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentSummary ownAttendance(String email) {
        Student student = students.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Student account not found."));
        if (!"STUDENT".equals(student.getRole())) throw new AccessDeniedException("Student attendance is available only to student accounts.");
        List<AttendanceRecord> ownRecords = records.findByStudentIdOrderBySessionHeldOnDesc(student.getId());
        Map<Long, List<AttendanceRecord>> grouped = ownRecords.stream().collect(Collectors.groupingBy(
                r -> r.getSession().getAssignment().getSubject().getId(), LinkedHashMap::new, Collectors.toList()));
        List<SubjectSummary> subjects = grouped.entrySet().stream().map(entry -> {
            List<AttendanceRecord> values = entry.getValue();
            Subject subject = values.get(0).getSession().getAssignment().getSubject();
            int attended = (int) values.stream().filter(AttendanceRecord::isPresent).count();
            int held = values.size();
            return new SubjectSummary(subject.getId(), subject.getSubjectCode(), subject.getName(), held, attended,
                    held - attended, percentage(attended, held));
        }).toList();
        int held = ownRecords.size();
        int attended = (int) ownRecords.stream().filter(AttendanceRecord::isPresent).count();
        List<StudentSession> ownSessions = ownRecords.stream().map(r -> {
            AttendanceSession session = r.getSession();
            TeachingAssignment a = session.getAssignment();
            return new StudentSession(session.getId(), session.getHeldOn(), a.getSubject().getSubjectCode(),
                    a.getSubject().getName(), a.getLectureNumber(), a.getStartTime(), a.getEndTime(), r.isPresent());
        }).toList();
        return new StudentSummary(held, attended, held - attended, percentage(attended, held), subjects, ownSessions);
    }

    private TeachingAssignment assigned(Long id, String email) {
        return assignments.findByIdAndFacultyEmail(id, email)
                .orElseThrow(() -> new AccessDeniedException("This class is not assigned to your faculty account."));
    }

    private List<Student> rosterFor(TeachingAssignment assignment) {
        return enrollments.findByAcademicClassIdAndActiveTrueOrderByStudentNameAsc(assignment.getAcademicClass().getId())
                .stream().map(StudentEnrollment::getStudent).filter(s -> "STUDENT".equalsIgnoreCase(s.getRole())).toList();
    }

    private void validateRoster(TeachingAssignment assignment, List<AttendanceMark> marks) {
        List<Student> roster = rosterFor(assignment);
        Set<Long> rosterIds = roster.stream().map(Student::getId).collect(Collectors.toSet());
        Set<Long> supplied = new HashSet<>();
        for (AttendanceMark mark : marks) {
            if (!supplied.add(mark.studentId())) throw new IllegalArgumentException("Attendance contains a duplicate student.");
            if (!rosterIds.contains(mark.studentId())) throw new AccessDeniedException("Attendance includes a student outside this assigned class.");
        }
        if (!supplied.equals(rosterIds)) throw new IllegalArgumentException("Attendance must include every student in the assigned class.");
    }

    private void validateScheduledDate(TeachingAssignment assignment, LocalDate date) {
        if (!assignment.getDayOfWeek().equalsIgnoreCase(date.getDayOfWeek().name()))
            throw new IllegalArgumentException("The selected date does not match the scheduled " + assignment.getDayOfWeek().toLowerCase(Locale.ROOT) + " lecture.");
    }

    private AssignmentOption option(TeachingAssignment a) {
        AcademicClass c = a.getAcademicClass(); Subject s = a.getSubject();
        return new AssignmentOption(a.getId(), s.getId(), s.getSubjectCode(), s.getName(), c.getId(),
                c.getDepartment().getCode(), c.getDepartment().getName(), c.getAcademicYear(), c.getDivision(),
                c.getBatch(), a.getLectureNumber(), a.getDayOfWeek(), a.getStartTime(), a.getEndTime());
    }

    private RosterStudent rosterStudent(Student s, Boolean present) {
        return new RosterStudent(s.getId(), s.getName(), s.getEmail(), s.getDepartment(), s.getYear(),
                s.getDivision(), s.getBatch(), present);
    }

    private SessionResponse sessionResponse(AttendanceSession session) {
        TeachingAssignment a = session.getAssignment(); Subject subject = a.getSubject(); AcademicClass c = a.getAcademicClass();
        List<AttendanceRecord> saved = records.findBySessionIdOrderByStudentNameAsc(session.getId());
        int present = (int) saved.stream().filter(AttendanceRecord::isPresent).count();
        return new SessionResponse(session.getId(), session.getHeldOn(), a.getId(), subject.getSubjectCode(), subject.getName(),
                c.getDepartment().getCode(), c.getAcademicYear(), c.getDivision(), c.getBatch(), a.getLectureNumber(),
                present, saved.size() - present, saved.size(), percentage(present, saved.size()),
                session.getCreatedAt(), session.getUpdatedAt(),
                saved.stream().map(r -> rosterStudent(r.getStudent(), r.isPresent())).toList());
    }

    private double percentage(int attended, int held) {
        return held == 0 ? 0 : Math.round(attended * 1000.0 / held) / 10.0;
    }
}
