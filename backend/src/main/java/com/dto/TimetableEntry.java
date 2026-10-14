package com.dto;
import com.entity.TeachingAssignment;
public record TimetableEntry(Long assignmentId, Long subjectId, String subjectCode, String subjectName,
    Integer semester, String syllabus, Long classId, String departmentCode, String academicYear,
    String division, String batch, String dayOfWeek, String startTime, String endTime, String classroom,
    String facultyName) {
    public static TimetableEntry from(TeachingAssignment a) {
        var s=a.getSubject(); var c=a.getAcademicClass();
        return new TimetableEntry(a.getId(),s.getId(),s.getSubjectCode(),s.getName(),s.getSemester(),s.getSyllabus(),
            c.getId(),c.getDepartment().getCode(),c.getAcademicYear(),c.getDivision(),c.getBatch(),
            a.getDayOfWeek(),a.getStartTime(),a.getEndTime(),a.getClassroom(),a.getFaculty().getName());
    }
}
