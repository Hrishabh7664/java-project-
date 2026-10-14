package com.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="election_audit")
public class ElectionAudit{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @Column(name="election_id",nullable=false)private Long electionId;
 @Column(nullable=false,length=40)private String action;
 @Column(nullable=false,length=254)private String actor;
 @Column(columnDefinition="TEXT")private String detail;
 @Column(nullable=false)private Instant createdAt=Instant.now();
 protected ElectionAudit(){}public ElectionAudit(Long id,String action,String actor,String detail){electionId=id;this.action=action;this.actor=actor;this.detail=detail;}
 public Long getId(){return id;}public Long getElectionId(){return electionId;}public String getAction(){return action;}public String getActor(){return actor;}public String getDetail(){return detail;}public Instant getCreatedAt(){return createdAt;}
}
