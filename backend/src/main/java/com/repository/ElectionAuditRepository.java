package com.repository;
import com.entity.ElectionAudit;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface ElectionAuditRepository extends JpaRepository<ElectionAudit,Long>{List<ElectionAudit> findByElectionIdOrderByCreatedAtDesc(Long id);}
