package com.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class AttendanceDtos {
    private AttendanceDtos() {}
    public record AssignmentOption(Long id, Long subjectId, String subjectCode, String subjectName, Long classId,
                                   String departmentCode, String departmentName, String academicYear,
                                   String division, String batch, Integer lectureNumber, String dayOfWeek,
                                   String startTime, String endTime) {}
    public record RosterStudent(Long id, String name, String email, String department, String academicYear,
                                String division, String batch, Boolean present) {}
    public record RosterResponse(Long assignmentId, Long sessionId, LocalDate date, List<RosterStudent> students) {}
    public record AttendanceMark(@NotNull Long studentId, @NotNull Boolean present) {}
    public record SaveRequest(@NotNull Long assignmentId, @NotNull LocalDate date,
                              @NotEmpty List<@NotNull @Valid AttendanceMark> attendance) {}
    public record SessionResponse(Long id, LocalDate date, Long assignmentId, String subjectCode,
                                  String subjectName, String departmentCode, String academicYear,
                                  String division, String batch, Integer lectureNumber,
                                  int presentCount, int absentCount, int totalCount, double percentage,
                                  Instant createdAt, Instant updatedAt, List<RosterStudent> attendance) {}
    public record SubjectSummary(Long subjectId, String subjectCode, String subjectName,
                                 int classesHeld, int classesAttended, int classesMissed, double percentage) {}
    public record StudentSummary(int classesHeld, int classesAttended, int classesMissed,
                                 double overallPercentage, List<SubjectSummary> subjects,
                                 List<StudentSession> sessions) {}
}
