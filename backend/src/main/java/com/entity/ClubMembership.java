package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="club_memberships",uniqueConstraints=@UniqueConstraint(name="uk_club_member",columnNames={"club_id","student_id"}))
public class ClubMembership {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="club_id",nullable=false) private Club club;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="student_id",nullable=false) private Student student;
    @Column(nullable=false) private boolean active=true;
    @Column(name="joined_at",nullable=false) private Instant joinedAt;
    protected ClubMembership(){}
    public ClubMembership(Club club,Student student,Instant joinedAt){this.club=club;this.student=student;this.joinedAt=joinedAt;}
    public Long getId(){return id;} public Club getClub(){return club;} public Student getStudent(){return student;}
    public boolean isActive(){return active;} public Instant getJoinedAt(){return joinedAt;} public void setActive(boolean active){this.active=active;}
}
