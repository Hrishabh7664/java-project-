package com.repository;
import com.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
 List<AttendanceRecord> findBySessionIdOrderByStudentNameAsc(Long sessionId);
 List<AttendanceRecord> findBySessionAssignmentIdAndStudentId(Long assignmentId, Long studentId);
 List<AttendanceRecord> findBySessionAssignmentSubjectIdAndStudentId(Long subjectId, Long studentId);
 List<AttendanceRecord> findBySessionId(Long sessionId);
 List<AttendanceRecord> findByStudentIdOrderBySessionHeldOnDesc(Long studentId);
}
