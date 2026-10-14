package com.entity;
import jakarta.persistence.*;
@Entity @Table(name="election_candidates")
public class ElectionCandidate{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="election_id",nullable=false) private Election election;
 @Column(nullable=false,length=160) private String name;
 @Column(length=120) private String department;
 @Column(name="candidate_year",length=20) private String year;
 @Column(columnDefinition="TEXT") private String manifesto;
 @Column(nullable=false) private boolean approved=true;
 protected ElectionCandidate(){}
 public ElectionCandidate(Election e,String n,String d,String y,String m){election=e;name=n;department=d;year=y;manifesto=m;}
 public Long getId(){return id;} public Election getElection(){return election;} public String getName(){return name;} public void setName(String v){name=v;}
 public String getDepartment(){return department;} public void setDepartment(String v){department=v;} public String getYear(){return year;} public void setYear(String v){year=v;}
 public String getManifesto(){return manifesto;} public void setManifesto(String v){manifesto=v;} public boolean isApproved(){return approved;} public void setApproved(boolean v){approved=v;}
}
