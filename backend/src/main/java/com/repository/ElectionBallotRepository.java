package com.repository;
import com.entity.ElectionBallot;import org.springframework.data.jpa.repository.JpaRepository;import java.util.Optional;
public interface ElectionBallotRepository extends JpaRepository<ElectionBallot,Long>{boolean existsByElectionIdAndStudentId(Long electionId,Long studentId);long countByElectionId(Long electionId);long countByCandidateId(Long candidateId);Optional<ElectionBallot> findByElectionIdAndStudentId(Long electionId,Long studentId);}
