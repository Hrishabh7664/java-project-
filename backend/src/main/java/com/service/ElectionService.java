package com.service;

import com.dto.ElectionDtos.*;
import com.entity.*;
import com.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service
public class ElectionService {
 private final ElectionRepository elections;private final ElectionCandidateRepository candidates;private final ElectionBallotRepository ballots;private final ElectionAuditRepository audits;private final StudentRepository students;
 public ElectionService(ElectionRepository e,ElectionCandidateRepository c,ElectionBallotRepository b,ElectionAuditRepository a,StudentRepository s){elections=e;candidates=c;ballots=b;audits=a;students=s;}
 public List<ElectionResponse> list(String email,boolean admin){return elections.findAll().stream().map(e->view(e,email,admin)).toList();}
 public ElectionResponse get(Long id,String email,boolean admin){return view(require(id),email,admin);}
 @Transactional public Election create(Election e,String actor){validateSchedule(e.getStartDate(),e.getEndDate());e.setEligibleDepartments(orAll(e.getEligibleDepartments()));e.setEligibleYears(orAll(e.getEligibleYears()));e.setStatus("DRAFT");e.setResultsPublished(false);e.setCreatedBy(actor);e.setUpdatedAt(Instant.now());Election saved=elections.save(e);audit(saved,"CREATED",actor,"Election created");return saved;}
 @Transactional public Election update(Long id,Election d,String actor){Election e=require(id);if(!"DRAFT".equals(e.getStatus()))throw new IllegalArgumentException("Only draft elections can be edited");validateSchedule(d.getStartDate(),d.getEndDate());e.setTitle(d.getTitle());e.setPosition(d.getPosition());e.setDescription(d.getDescription());e.setStartDate(d.getStartDate());e.setEndDate(d.getEndDate());e.setEligibleDepartments(orAll(d.getEligibleDepartments()));e.setEligibleYears(orAll(d.getEligibleYears()));e.setUpdatedAt(Instant.now());audit(e,"UPDATED",actor,"Election configuration updated");return elections.save(e);}
 @Transactional public ElectionCandidate addCandidate(Long id,CandidateRequest r,String actor){Election e=require(id);if(!"DRAFT".equals(e.getStatus()))throw new IllegalArgumentException("Candidates can only be configured while the election is a draft");if(r.name()==null||r.name().isBlank())throw new IllegalArgumentException("Candidate name is required");var c=candidates.save(new ElectionCandidate(e,r.name().trim(),r.department(),r.year(),r.manifesto()));audit(e,"CANDIDATE_ADDED",actor,"Candidate "+c.getId()+" added");return c;}
 @Transactional public ElectionCandidate updateCandidateStatus(Long electionId,Long candidateId,boolean approved,String actor){Election e=require(electionId);if(!"DRAFT".equals(e.getStatus()))throw new IllegalArgumentException("Candidates can only be reviewed while the election is a draft");ElectionCandidate c=candidates.findByIdAndElectionId(candidateId,electionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidate not found"));c.setApproved(approved);audit(e,approved?"CANDIDATE_APPROVED":"CANDIDATE_REJECTED",actor,"Candidate "+candidateId+" review updated");return candidates.save(c);}
 @Transactional public Election lifecycle(Long id,LifecycleRequest r,String actor){Election e=elections.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Election not found"));String next=Objects.toString(r.status(),e.getStatus()).toUpperCase(Locale.ROOT);String current=e.getStatus();
  if("DRAFT".equals(current)&&"OPEN".equals(next)){if(candidates.findByElectionIdAndApprovedTrue(id).isEmpty())throw new IllegalArgumentException("An election needs at least one approved candidate before opening");}
  else if("OPEN".equals(current)&&"CLOSED".equals(next)){}
  else if(current.equals(next)){}
  else throw new IllegalArgumentException("Allowed lifecycle: DRAFT to OPEN to CLOSED");
  e.setStatus(next);if(Boolean.TRUE.equals(r.publishResults())){if(!"CLOSED".equals(next))throw new IllegalArgumentException("Results can only be published after closing the election");e.setResultsPublished(true);}
  if(Boolean.FALSE.equals(r.publishResults()))e.setResultsPublished(false);e.setUpdatedAt(Instant.now());audit(e,"STATUS_"+next,actor,Boolean.TRUE.equals(r.publishResults())?"Results published":"Lifecycle updated");return elections.save(e);
 }
 @Transactional public void vote(Long id,Long candidateId,String email){Election e=elections.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Election not found"));if(!"OPEN".equals(e.getStatus())||!inWindow(e))throw new IllegalArgumentException("This election is not currently open for voting");Student s=students.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Student not found"));if(!"STUDENT".equals(s.getRole()))throw new AccessDeniedException("Only students can vote");if(!eligible(e,s))throw new AccessDeniedException("You are not eligible for this election");if(ballots.existsByElectionIdAndStudentId(id,s.getId()))throw new com.service.ConflictException("You have already voted in this election");ElectionCandidate c=candidates.findByIdAndElectionId(candidateId,id).orElseThrow(()->new IllegalArgumentException("Candidate is not in this election"));if(!c.isApproved())throw new IllegalArgumentException("Candidate is not approved");ballots.saveAndFlush(new ElectionBallot(e,c,s));}
 public boolean hasVoted(Long id,String email){Student s=students.findByEmailIgnoreCase(email).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return ballots.existsByElectionIdAndStudentId(id,s.getId());}
 public ResultsResponse results(Long id){Election e=require(id);if(!e.isResultsPublished())throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Results have not been published");List<CandidateResponse> rows=candidates.findByElectionIdAndApprovedTrue(id).stream().map(c->new CandidateResponse(c.getId(),c.getName(),c.getDepartment(),c.getYear(),c.getManifesto(),ballots.countByCandidateId(c.getId()),true)).toList();return new ResultsResponse(id,e.getTitle(),rows,ballots.countByElectionId(id));}
 public List<ElectionAudit> history(Long id){require(id);return audits.findByElectionIdOrderByCreatedAtDesc(id);}
 @Transactional public boolean deleteDraft(Long id,String actor){Election e=require(id);if(!"DRAFT".equals(e.getStatus())||!candidates.findByElectionId(id).isEmpty())return false;audit(e,"DELETED",actor,"Draft election deleted");elections.delete(e);return true;}
 private ElectionResponse view(Election e,String email,boolean admin){Student s=students.findByEmailIgnoreCase(email).orElse(null);boolean ok=s!=null&&eligible(e,s);List<ElectionCandidate> source=admin?candidates.findByElectionId(e.getId()):candidates.findByElectionIdAndApprovedTrue(e.getId());List<CandidateResponse> cs=source.stream().map(c->new CandidateResponse(c.getId(),c.getName(),c.getDepartment(),c.getYear(),c.getManifesto(),e.isResultsPublished()?ballots.countByCandidateId(c.getId()):0,c.isApproved())).toList();return new ElectionResponse(e.getId(),e.getTitle(),e.getPosition(),e.getDescription(),e.getStartDate(),e.getEndDate(),e.getStatus(),e.getEligibleDepartments(),e.getEligibleYears(),ok,s!=null&&ballots.existsByElectionIdAndStudentId(e.getId(),s.getId()),e.isResultsPublished(),admin?ballots.countByElectionId(e.getId()):0,cs);}
 private Election require(Long id){return elections.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Election not found"));}
 private boolean eligible(Election e,Student s){return matches(e.getEligibleDepartments(),s.getDepartment())&&matches(e.getEligibleYears(),s.getYear());}
 private boolean matches(String configured,String actual){if(configured==null||configured.isBlank()||configured.equalsIgnoreCase("ALL"))return true;if(actual==null)return false;return Arrays.stream(configured.split(",")).map(String::trim).anyMatch(value->value.equalsIgnoreCase(actual));}
 private boolean inWindow(Election e){try{LocalDateTime now=LocalDateTime.now();return !now.isBefore(LocalDateTime.parse(e.getStartDate()))&&!now.isAfter(LocalDateTime.parse(e.getEndDate()));}catch(Exception x){return false;}}
 private void validateSchedule(String start,String end){try{if(LocalDateTime.parse(start).isAfter(LocalDateTime.parse(end)))throw new IllegalArgumentException("Voting end must be after start");}catch(java.time.format.DateTimeParseException ex){throw new IllegalArgumentException("Voting dates must use ISO local date-time format");}}
 private String orAll(String s){return s==null||s.isBlank()?"ALL":s.trim();}
 private void audit(Election e,String action,String actor,String detail){audits.save(new ElectionAudit(e.getId(),action,actor,detail));}
}
