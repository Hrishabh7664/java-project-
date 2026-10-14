package com.repository;
import com.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface PostVoteRepository extends JpaRepository<PostVote,Long> {
 Optional<PostVote> findByPostIdAndUserEmailIgnoreCase(Long postId,String email);
 long countByPostIdAndDirection(Long postId,int direction);
 @Query("select coalesce(sum(v.direction),0) from PostVote v where v.postId=:postId") long score(@Param("postId") Long postId);
}
