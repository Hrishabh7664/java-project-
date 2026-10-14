package com.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="comments", uniqueConstraints=@UniqueConstraint(name="uk_comment_demo_key",columnNames="demo_key"))
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private String authorName;

    @JsonIgnore
    @Column(length=254)
    private String authorEmail;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String createdAt;
    @Column(nullable = false)
    private boolean hidden = false;
    @Column(columnDefinition = "TEXT")
    private String moderationReason;
    private String moderatedBy;
    private java.time.Instant moderatedAt;
    @Column(name="demo_key",unique=true,length=80)
    private String demoKey;

    public Comment() {
    }

    public Comment(Long postId, String authorName, String content, String createdAt) {
        this.postId = postId;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }
    public String getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(String moderatedBy) { this.moderatedBy = moderatedBy; }
    public java.time.Instant getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(java.time.Instant moderatedAt) { this.moderatedAt = moderatedAt; }
    public String getDemoKey(){return demoKey;} public void setDemoKey(String key){demoKey=key;}
}
