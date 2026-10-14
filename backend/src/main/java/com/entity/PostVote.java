package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "post_votes", uniqueConstraints = @UniqueConstraint(name = "uk_post_vote_user_post", columnNames = {"post_id", "user_email"}), indexes = @Index(name="idx_post_vote_post_direction",columnList="post_id,direction"))
public class PostVote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "post_id", nullable = false) private Long postId;
    @Column(name = "user_email", nullable = false, length = 254) private String userEmail;
    @Column(nullable = false) private int direction;
    @Column(nullable = false) private Instant updatedAt;
    protected PostVote() {}
    public PostVote(Long postId, String userEmail, int direction, Instant updatedAt) { this.postId=postId; this.userEmail=userEmail; this.direction=direction; this.updatedAt=updatedAt; }
    public Long getId(){return id;} public Long getPostId(){return postId;} public String getUserEmail(){return userEmail;}
    public int getDirection(){return direction;} public void setDirection(int direction){this.direction=direction;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant updatedAt){this.updatedAt=updatedAt;}
}
