package com.repository;
import com.entity.ClubApplication;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ClubApplicationRepository extends JpaRepository<ClubApplication,Long> {
    List<ClubApplication> findByStudentEmailIgnoreCaseOrderBySubmittedAtDesc(String email);
    List<ClubApplication> findByClubIdAndStatusOrderBySubmittedAtAsc(Long clubId,String status);
    List<ClubApplication> findByClubIdOrderBySubmittedAtDesc(Long clubId);
    Optional<ClubApplication> findFirstByClubIdAndStudentIdOrderBySubmittedAtDesc(Long clubId,Long studentId);
    boolean existsByClubIdAndStudentIdAndStatus(Long clubId,Long studentId,String status);
    long countByClubId(Long clubId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from ClubApplication c where c.id=:id") Optional<ClubApplication> lockById(@Param("id") Long id);
}
