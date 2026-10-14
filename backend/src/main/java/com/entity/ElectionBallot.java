package com.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="election_ballots",uniqueConstraints=@UniqueConstraint(name="uk_election_ballot_student",columnNames={"election_id","student_id"}))
public class ElectionBallot{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="election_id",nullable=false) private Election election;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="candidate_id",nullable=false) private ElectionCandidate candidate;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="student_id",nullable=false) private Student student;
 @Column(nullable=false) private Instant castAt;
 protected ElectionBallot(){}
 public ElectionBallot(Election e,ElectionCandidate c,Student s){election=e;candidate=c;student=s;castAt=Instant.now();}
 public Long getId(){return id;} public Election getElection(){return election;} public ElectionCandidate getCandidate(){return candidate;} public Student getStudent(){return student;} public Instant getCastAt(){return castAt;}
}
