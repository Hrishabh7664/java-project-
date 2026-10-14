package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "content_reports")
public class ContentReport {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="content_type",nullable=false,length=16) private String contentType;
    @Column(name="content_id",nullable=false) private Long contentId;
    @Column(name="reporter_email",nullable=false,length=254) private String reporterEmail;
    @Column(nullable=false,length=1000) private String reason;
    @Column(nullable=false,length=24) private String status="OPEN";
    @Column(length=1000) private String moderationReason;
    @Column(length=254) private String resolvedBy;
    private Instant createdAt;
    private Instant resolvedAt;
    protected ContentReport(){}
    public ContentReport(String contentType,Long contentId,String reporterEmail,String reason){this.contentType=contentType;this.contentId=contentId;this.reporterEmail=reporterEmail;this.reason=reason;this.createdAt=Instant.now();}
    public Long getId(){return id;} public String getContentType(){return contentType;} public Long getContentId(){return contentId;}
    public String getReporterEmail(){return reporterEmail;} public String getReason(){return reason;} public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;} public String getModerationReason(){return moderationReason;} public void setModerationReason(String v){moderationReason=v;}
    public String getResolvedBy(){return resolvedBy;} public void setResolvedBy(String v){resolvedBy=v;} public Instant getCreatedAt(){return createdAt;}
    public Instant getResolvedAt(){return resolvedAt;} public void setResolvedAt(Instant v){resolvedAt=v;}
}
