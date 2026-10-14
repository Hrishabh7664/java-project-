package com.controller;

import com.entity.Comment;
import com.service.CommentService;
import com.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import com.repository.PostRepository;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final StudentRepository studentRepository;
    private final PostRepository postRepository;

    public CommentController(CommentService commentService, StudentRepository studentRepository, PostRepository postRepository) {
        this.commentService = commentService;
        this.studentRepository = studentRepository;
        this.postRepository = postRepository;
    }

    @GetMapping
    public List<Comment> getAllComments(@RequestParam(required = false) Long postId) {
        if (postId != null) {
            return commentService.getCommentsByPostId(postId);
        }
        return commentService.getAllComments().stream().filter(c -> !c.isHidden()).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment, Authentication authentication) {
        if (comment.getPostId() == null || comment.getContent() == null || comment.getContent().isBlank()) return ResponseEntity.badRequest().build();
        if(postRepository.findById(comment.getPostId()).filter(post->!post.isHidden()).isEmpty())return ResponseEntity.notFound().build();
        comment.setAuthorName(studentRepository.findByEmailIgnoreCase(authentication.getName())
                .map(student -> student.getName()).orElse(authentication.getName()));
        comment.setAuthorEmail(authentication.getName());
        comment.setId(null);comment.setCreatedAt(null);comment.setHidden(false);comment.setDemoKey(null);comment.setModeratedAt(null);comment.setModeratedBy(null);comment.setModerationReason(null);
        Comment created = commentService.createComment(comment);
        return ResponseEntity
                .created(URI.create("/api/comments/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id,@RequestBody Comment changes,Authentication authentication){
        Comment current=commentService.getAnyCommentById(id);if(current==null||current.isHidden())return ResponseEntity.notFound().build();
        boolean admin=authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));
        if(!admin&&(current.getAuthorEmail()==null||!authentication.getName().equalsIgnoreCase(current.getAuthorEmail())))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You can only edit your own comments");
        if(changes.getContent()==null||changes.getContent().isBlank()||changes.getContent().length()>2000)return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(commentService.updateOwnComment(id,changes.getContent().trim()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        Comment existing = commentService.getAnyCommentById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean owner = existing.getAuthorEmail()!=null && authentication.getName().equalsIgnoreCase(existing.getAuthorEmail());
        if (!admin && !owner) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own comments");
        return commentService.deleteComment(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
