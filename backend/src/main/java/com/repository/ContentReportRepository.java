package com.repository;
import com.entity.ContentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ContentReportRepository extends JpaRepository<ContentReport,Long> {
 List<ContentReport> findAllByOrderByCreatedAtDesc();
 Optional<ContentReport> findByReporterEmailIgnoreCaseAndContentTypeAndContentId(String email,String type,Long id);
 long countByReporterEmailIgnoreCaseAndContentTypeAndContentId(String email,String type,Long id);
}
