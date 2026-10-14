package com.repository;

import com.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, Long> {
    List<TeachingAssignment> findByFacultyEmailOrderByAcademicClassDepartmentCodeAscAcademicClassAcademicYearAscSubjectSubjectCodeAsc(String email);
    Optional<TeachingAssignment> findByIdAndFacultyEmail(Long id, String email);
    Optional<TeachingAssignment> findFirstBySubjectIdAndAcademicClassId(Long subjectId, Long classId);
    boolean existsBySubjectIdAndAcademicClassIdAndLectureNumber(Long subjectId, Long academicClassId, Integer lectureNumber);
    boolean existsByFacultyEmailAndSubjectIdAndAcademicClassId(String email, Long subjectId, Long classId);
    boolean existsByFacultyEmailAndSubjectId(String email, Long subjectId);
    List<TeachingAssignment> findByFacultyEmailOrderByDayOfWeekAscStartTimeAsc(String email);
    List<TeachingAssignment> findByAcademicClassIdInOrderByDayOfWeekAscStartTimeAsc(List<Long> classIds);
}
