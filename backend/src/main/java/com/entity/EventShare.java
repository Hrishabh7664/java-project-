package com.entity;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="event_shares")
public class EventShare{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY)private Long id;
 @ManyToOne(optional=false,fetch=FetchType.EAGER)@JoinColumn(name="event_id",nullable=false)private Event event;
 @Column(nullable=false,length=180)private String destinationCampus;
 @Column(nullable=false,length=20)private String status="ACTIVE";
 @Column(nullable=false,length=254)private String createdBy;
 private Instant createdAt=Instant.now();private Instant revokedAt;
 @Column(length=254)private String revokedBy;
 protected EventShare(){}public EventShare(Event e,String campus,String actor){event=e;destinationCampus=campus;createdBy=actor;}
 public Long getId(){return id;}public Event getEvent(){return event;}public String getDestinationCampus(){return destinationCampus;}public String getStatus(){return status;}public String getCreatedBy(){return createdBy;}public Instant getCreatedAt(){return createdAt;}public Instant getRevokedAt(){return revokedAt;}public String getRevokedBy(){return revokedBy;}public void revoke(String actor){status="REVOKED";revokedBy=actor;revokedAt=Instant.now();}
}
