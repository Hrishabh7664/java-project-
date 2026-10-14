package com.repository;
import com.entity.SubmissionVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface SubmissionVersionRepository extends JpaRepository<SubmissionVersion, Long> {
    List<SubmissionVersion> findBySubmissionIdOrderByVersionNumberAsc(Long submissionId);
    Optional<SubmissionVersion> findBySubmissionIdAndVersionNumber(Long submissionId, Integer versionNumber);
}
