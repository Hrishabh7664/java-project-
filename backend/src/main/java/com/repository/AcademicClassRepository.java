package com.repository;
import com.entity.AcademicClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AcademicClassRepository extends JpaRepository<AcademicClass, Long> {
 Optional<AcademicClass> findByDepartmentCodeAndAcademicYearAndDivisionAndBatch(String departmentCode, String academicYear, String division, String batch);
}
