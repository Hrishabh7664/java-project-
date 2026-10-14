package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="club_applications", indexes={@Index(name="idx_club_application_club_status",columnList="club_id,status"),@Index(name="idx_club_application_student",columnList="student_id,submitted_at")})
public class ClubApplication {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="club_id",nullable=false) private Club club;
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="student_id",nullable=false) private Student student;
    @Column(nullable=false,length=24) private String status="PENDING";
    @Column(name="motivation",columnDefinition="TEXT") private String motivation;
    @Column(name="submitted_at",nullable=false) private Instant submittedAt;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="decided_by_student_id") private Student decidedBy;
    @Column(name="decided_at") private Instant decidedAt;
    @Column(name="decision_note",columnDefinition="TEXT") private String decisionNote;
    protected ClubApplication() {}
    public ClubApplication(Club club,Student student,String motivation,Instant submittedAt){this.club=club;this.student=student;this.motivation=motivation;this.submittedAt=submittedAt;}
    public Long getId(){return id;} public Club getClub(){return club;} public Student getStudent(){return student;}
    public String getStatus(){return status;} public String getMotivation(){return motivation;} public Instant getSubmittedAt(){return submittedAt;}
    public Student getDecidedBy(){return decidedBy;} public Instant getDecidedAt(){return decidedAt;} public String getDecisionNote(){return decisionNote;}
    public void decide(String status,Student leader,String note,Instant at){this.status=status;this.decidedBy=leader;this.decisionNote=note;this.decidedAt=at;}
    public void withdraw(Student applicant,Instant at){this.status="WITHDRAWN";this.decidedBy=applicant;this.decidedAt=at;this.decisionNote="Withdrawn by applicant.";}
}
