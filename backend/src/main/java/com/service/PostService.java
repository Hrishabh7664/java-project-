package com.service;

import com.entity.Post;
import com.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findByHiddenFalseOrderByCreatedAtDesc();
    }
    public List<Post> getByCategory(String category) { return postRepository.findByHiddenFalseAndCategoryIgnoreCaseOrderByCreatedAtDesc(category); }

    public Post getPostById(Long id) {
        return postRepository.findById(id).filter(post -> !post.isHidden()).orElse(null);
    }
    public Post getAnyPostById(Long id) { return postRepository.findById(id).orElse(null); }

    public Post createPost(Post post) {
        if (post.getCreatedAt() == null || post.getCreatedAt().isBlank()) {
            post.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        post.setVoteCount(0);
        post.setHidden(false);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post postDetails) {
        Post existing = postRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(postDetails.getTitle());
        existing.setContent(postDetails.getContent());
        existing.setCategory(postDetails.getCategory());
        existing.setHidden(postDetails.isHidden());existing.setModeratedBy(postDetails.getModeratedBy());existing.setModeratedAt(postDetails.getModeratedAt());existing.setModerationReason(postDetails.getModerationReason());existing.setDemoKey(postDetails.getDemoKey());
        if (postDetails.getAuthorName() != null) {
            existing.setAuthorName(postDetails.getAuthorName());
        }

        return postRepository.save(existing);
    }

    public boolean deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            return false;
        }
        postRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Post moderatePost(Long id, boolean hidden, String admin, String reason) {
        Post post=postRepository.findById(id).orElse(null); if(post==null)return null;
        post.setHidden(hidden);post.setModeratedBy(admin);post.setModeratedAt(java.time.Instant.now());post.setModerationReason(reason);
        return postRepository.save(post);
    }

}
