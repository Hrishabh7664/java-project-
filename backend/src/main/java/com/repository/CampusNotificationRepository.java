package com.repository;
import com.entity.CampusNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CampusNotificationRepository extends JpaRepository<CampusNotification,Long> {
    List<CampusNotification> findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(String email);
    Optional<CampusNotification> findByIdAndStudentEmailIgnoreCase(Long id,String email);
}
