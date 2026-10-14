package com.service;

import com.entity.Comment;
import com.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdAndHiddenFalse(postId);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).filter(c -> !c.isHidden()).orElse(null);
    }

    public Comment getAnyCommentById(Long id) { return commentRepository.findById(id).orElse(null); }
    public List<Comment> getAllCommentsForModeration() { return commentRepository.findAll(); }
    public Comment moderateComment(Long id,boolean hidden,String admin,String reason) {
        Comment comment=commentRepository.findById(id).orElse(null);if(comment==null)return null;
        comment.setHidden(hidden);comment.setModeratedBy(admin);comment.setModeratedAt(java.time.Instant.now());comment.setModerationReason(reason);
        return commentRepository.save(comment);
    }

    public Comment updateOwnComment(Long id,String content){
        Comment comment=getAnyCommentById(id);if(comment==null||comment.isHidden())return null;
        comment.setContent(content);return commentRepository.save(comment);
    }

    public Comment createComment(Comment comment) {
        if (comment.getCreatedAt() == null || comment.getCreatedAt().isBlank()) {
            comment.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        return commentRepository.save(comment);
    }

    public boolean deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            return false;
        }
        commentRepository.deleteById(id);
        return true;
    }
}
