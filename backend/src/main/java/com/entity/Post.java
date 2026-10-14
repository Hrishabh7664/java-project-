package com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "posts", uniqueConstraints=@UniqueConstraint(name="uk_post_demo_key",columnNames="demo_key"))
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String authorName;

    @JsonIgnore
    @Column(length=254)
    private String authorEmail;

    @NotBlank(message = "Title is required")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category;
    private String createdAt;
    @jakarta.persistence.Transient
    private Integer voteCount = 0;
    @Column(nullable = false)
    private boolean hidden = false;
    @Column(columnDefinition = "TEXT")
    private String moderationReason;
    private String moderatedBy;
    private java.time.Instant moderatedAt;
    @Column(name="demo_key",unique=true,length=80)
    private String demoKey;
    @jakarta.persistence.Transient private Integer userVote = 0;
    @jakarta.persistence.Transient private Long upvotes = 0L;
    @jakarta.persistence.Transient private Long downvotes = 0L;

    public Post() {
    }

    public Post(String authorName, String title, String content, String category, String createdAt, Integer voteCount) {
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = createdAt;
        this.voteCount = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
    public String getModerationReason() { return moderationReason; }
    public void setModerationReason(String moderationReason) { this.moderationReason = moderationReason; }
    public String getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(String moderatedBy) { this.moderatedBy = moderatedBy; }
    public java.time.Instant getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(java.time.Instant moderatedAt) { this.moderatedAt = moderatedAt; }
    public Integer getUserVote(){return userVote;} public void setUserVote(Integer value){userVote=value;}
    public Long getUpvotes(){return upvotes;} public void setUpvotes(Long value){upvotes=value;}
    public Long getDownvotes(){return downvotes;} public void setDownvotes(Long value){downvotes=value;}
    public String getDemoKey(){return demoKey;} public void setDemoKey(String key){demoKey=key;}
}
