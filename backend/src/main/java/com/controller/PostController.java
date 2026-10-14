package com.controller;

import com.entity.Post;
import com.service.PostService;
import com.repository.StudentRepository;
import com.service.VoteService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final StudentRepository studentRepository;
    private final VoteService voteService;

    public PostController(PostService postService, StudentRepository studentRepository, VoteService voteService) {
        this.postService = postService;
        this.studentRepository = studentRepository;
        this.voteService = voteService;
    }

    @GetMapping
    public List<Post> getAllPosts(@RequestParam(required=false) String category,
                                  @RequestParam(required=false) String search,
                                  @RequestParam(defaultValue="recent") String sort,
                                  @RequestParam(defaultValue="0") int page,
                                  @RequestParam(defaultValue="20") int size,
                                  Authentication authentication) {
        if(page<0||size<1||size>100||(long)page*size>Integer.MAX_VALUE)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid page or size");
        if(!sort.equalsIgnoreCase("recent")&&!sort.equalsIgnoreCase("popular"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Sort must be recent or popular");
        List<Post> all=category==null||category.isBlank()?postService.getAllPosts():postService.getByCategory(category);
        if(search!=null&&!search.isBlank()){String q=search.toLowerCase(java.util.Locale.ROOT);all=all.stream().filter(p->((p.getTitle()==null?"":p.getTitle())+" "+(p.getContent()==null?"":p.getContent())+" "+(p.getCategory()==null?"":p.getCategory())).toLowerCase(java.util.Locale.ROOT).contains(q)).toList();}
        all=all.stream().peek(post -> {
            var state=voteService.state(post.getId(),authentication.getName());post.setVoteCount((int)state.score());
            post.setUserVote(state.userVote());post.setUpvotes(state.upvotes());post.setDownvotes(state.downvotes());
        }).toList();
        if(sort.equalsIgnoreCase("popular"))all=all.stream().sorted((a,b)->Integer.compare(b.getVoteCount(),a.getVoteCount())).toList();
        int from=Math.min(page*size,all.size()),to=Math.min(from+size,all.size());return all.subList(from,to);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id, Authentication authentication) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        var state=voteService.state(id,authentication.getName());post.setVoteCount((int)state.score());
        post.setUserVote(state.userVote());post.setUpvotes(state.upvotes());post.setDownvotes(state.downvotes());
        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody Post post, Authentication authentication) {
        post.setAuthorName(studentRepository.findByEmailIgnoreCase(authentication.getName())
                .map(student -> student.getName()).orElse(authentication.getName()));
        post.setAuthorEmail(authentication.getName());
        post.setId(null);post.setVoteCount(0);post.setHidden(false);post.setModeratedAt(null);post.setModeratedBy(null);post.setModerationReason(null);
        post.setUserVote(0);post.setUpvotes(0L);post.setDownvotes(0L);post.setDemoKey(null);
        Post created = postService.createPost(post);
        return ResponseEntity
                .created(URI.create("/api/posts/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}/vote")
    public com.dto.VoteResponse voteState(@PathVariable Long id, Authentication authentication) { return voteService.state(id,authentication.getName()); }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @Valid @RequestBody Post post, Authentication authentication) {
        Post existing = postService.getPostById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        requireOwnerOrAdmin(existing.getAuthorEmail(), authentication);
        post.setId(null);post.setHidden(existing.isHidden());post.setModeratedBy(existing.getModeratedBy());post.setModeratedAt(existing.getModeratedAt());post.setModerationReason(existing.getModerationReason());post.setDemoKey(existing.getDemoKey());
        post.setVoteCount(null);
        post.setAuthorName(existing.getAuthorName());
        Post updated = postService.updatePost(id, post);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<com.dto.VoteResponse> votePost(@PathVariable Long id, @RequestParam int direction, Authentication authentication) {
        return ResponseEntity.ok(voteService.vote(id,authentication.getName(),direction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        Post existing = postService.getPostById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        requireOwnerOrAdmin(existing.getAuthorEmail(), authentication);
        return postService.deletePost(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private void requireOwnerOrAdmin(String authorEmail, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean owner = authorEmail != null && authentication.getName().equalsIgnoreCase(authorEmail);
        if (!admin && !owner) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only manage your own posts");
    }
}
