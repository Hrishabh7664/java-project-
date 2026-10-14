package com.repository;

import com.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    java.util.List<Post> findByHiddenFalseOrderByCreatedAtDesc();
    java.util.List<Post> findByHiddenFalseAndCategoryIgnoreCaseOrderByCreatedAtDesc(String category);
}
