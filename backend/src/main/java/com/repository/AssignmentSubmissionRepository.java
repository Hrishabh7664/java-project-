package com.repository;
import com.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    List<AssignmentSubmission> findByAssignmentIdOrderBySubmittedAtDesc(Long assignmentId);
    List<AssignmentSubmission> findByAssignmentFacultyEmailOrderBySubmittedAtDesc(String email);
    long countByAssignmentId(Long assignmentId);
    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
    @Query("select s.assignment.id, count(s.id) from AssignmentSubmission s where s.assignment.id in :ids group by s.assignment.id")
    List<Object[]> countGroupedByAssignmentIds(@Param("ids") List<Long> assignmentIds);
}
