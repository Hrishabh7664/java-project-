package com.service;

import com.entity.Post;
import com.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Post createPost(Post post) {
        if (post.getCreatedAt() == null || post.getCreatedAt().isBlank()) {
            post.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        if (post.getVoteCount() == null) {
            post.setVoteCount(0);
        }
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
        if (postDetails.getAuthorName() != null) {
            existing.setAuthorName(postDetails.getAuthorName());
        }
        if (postDetails.getVoteCount() != null) {
            existing.setVoteCount(postDetails.getVoteCount());
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

    public Post votePost(Long id, int delta) {
        Post existing = postRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setVoteCount(existing.getVoteCount() + delta);
        return postRepository.save(existing);
    }
}
