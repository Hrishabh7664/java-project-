package com.repository;
import com.entity.AttendanceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
 Optional<AttendanceSession> findByAssignmentIdAndHeldOn(Long assignmentId, LocalDate heldOn);
 List<AttendanceSession> findByAssignmentFacultyEmailOrderByHeldOnDesc(String email);
}
