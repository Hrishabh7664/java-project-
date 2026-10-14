package com.repository;import com.entity.MagazineIssue;import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;import java.util.Optional;
public interface MagazineIssueRepository extends JpaRepository<MagazineIssue,Long>{List<MagazineIssue> findByPublishedTrueOrderByPublicationDateDesc();Optional<MagazineIssue> findByDemoKey(String key);}
