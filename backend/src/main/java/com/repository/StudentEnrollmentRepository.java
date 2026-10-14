package com.repository;
import com.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {
    boolean existsByStudentIdAndAcademicClassId(Long studentId, Long classId);
    List<StudentEnrollment> findByAcademicClassIdAndActiveTrueOrderByStudentNameAsc(Long classId);
    List<StudentEnrollment> findByStudentEmailIgnoreCaseAndActiveTrue(String email);
}
