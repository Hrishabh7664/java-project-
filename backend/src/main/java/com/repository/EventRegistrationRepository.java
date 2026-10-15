package com.repository;

import com.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventIdAndStudentId(Long eventId, Long studentId);
    long countByEventId(Long eventId);
    List<EventRegistration> findByEventIdOrderByRegisteredAtAsc(Long eventId);
    void deleteByEventIdAndStudentId(Long eventId, Long studentId);
    List<EventRegistration> findByStudentIdOrderByRegisteredAtDesc(Long studentId);
}
