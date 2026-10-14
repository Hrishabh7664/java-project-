package com.repository;
import com.entity.ClubMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ClubMembershipRepository extends JpaRepository<ClubMembership,Long> {
    Optional<ClubMembership> findByClubIdAndStudentId(Long clubId,Long studentId);
    boolean existsByClubIdAndStudentIdAndActiveTrue(Long clubId,Long studentId);
    long countByClubIdAndActiveTrue(Long clubId);
    long countByClubId(Long clubId);
    List<ClubMembership> findByClubIdAndActiveTrueOrderByJoinedAtAsc(Long clubId);
}
