package com.repository;
import com.entity.AttendanceChange;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceChangeRepository extends JpaRepository<AttendanceChange, Long> {}
