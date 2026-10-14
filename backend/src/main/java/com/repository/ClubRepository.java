package com.repository;

import com.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    Optional<Club> findByDemoKey(String demoKey);
    Optional<Club> findByNameIgnoreCase(String name);
    List<Club> findByLeaderEmailIgnoreCaseOrderByNameAsc(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from Club c where c.id=:id") Optional<Club> lockById(@Param("id") Long id);
}
