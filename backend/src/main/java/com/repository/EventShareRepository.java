package com.repository;
import com.entity.EventShare;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface EventShareRepository extends JpaRepository<EventShare,Long>{List<EventShare> findAllByOrderByCreatedAtDesc();List<EventShare> findByStatusOrderByCreatedAtDesc(String status);}
