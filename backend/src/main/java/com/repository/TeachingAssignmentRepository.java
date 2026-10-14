package com.repository;

import com.entity.TeachingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TeachingAssignmentRepository extends JpaRepository<TeachingAssignment, Long> {
    List<TeachingAssignment> findByFacultyEmailOrderByAcademicClassDepartmentCodeAscAcademicClassAcademicYearAscSubjectSubjectCodeAsc(String email);
    Optional<TeachingAssignment> findByIdAndFacultyEmail(Long id, String email);
    boolean existsBySubjectIdAndAcademicClassIdAndLectureNumber(Long subjectId, Long academicClassId, Integer lectureNumber);
}
