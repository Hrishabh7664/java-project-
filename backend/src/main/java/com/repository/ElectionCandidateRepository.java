package com.repository;
import com.entity.ElectionCandidate;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;import java.util.Optional;
public interface ElectionCandidateRepository extends JpaRepository<ElectionCandidate,Long>{List<ElectionCandidate> findByElectionIdAndApprovedTrue(Long id);List<ElectionCandidate> findByElectionId(Long id);Optional<ElectionCandidate> findByIdAndElectionId(Long id,Long electionId);}
