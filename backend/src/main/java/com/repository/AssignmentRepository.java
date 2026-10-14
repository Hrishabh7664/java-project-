package com.repository;

import com.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByFacultyEmailOrderByDeadlineAtAsc(String email);
    Optional<Assignment> findByIdAndFacultyEmail(Long id, String email);
    List<Assignment> findByTargetAcademicClassIdInAndStatusOrderByDeadlineAtAsc(List<Long> classIds, String status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Assignment a where a.id = :id")
    Optional<Assignment> findByIdForUpdate(@Param("id") Long id);
}
