package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="campus_notifications",indexes=@Index(name="idx_notification_student_created",columnList="student_id,created_at"))
public class CampusNotification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="student_id",nullable=false) private Student student;
    @Column(nullable=false,length=180) private String title;
    @Column(nullable=false,length=1000) private String message;
    @Column(length=500) private String link;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="is_read",nullable=false) private boolean read;
    protected CampusNotification(){}
    public CampusNotification(Student student,String title,String message,String link,Instant createdAt){this.student=student;this.title=title;this.message=message;this.link=link;this.createdAt=createdAt;}
    public Long getId(){return id;} public Student getStudent(){return student;} public String getTitle(){return title;} public String getMessage(){return message;}
    public String getLink(){return link;} public Instant getCreatedAt(){return createdAt;} public boolean isRead(){return read;} public void setRead(boolean read){this.read=read;}
}
