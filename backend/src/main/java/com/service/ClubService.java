package com.service;

import com.dto.ClubDtos.*;
import com.entity.*;
import com.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;

@Service
public class ClubService {
    private final ClubRepository clubs; private final StudentRepository students;
    private final ClubApplicationRepository applications; private final ClubMembershipRepository memberships;
    private final CampusNotificationRepository notifications;
    public ClubService(ClubRepository clubs,StudentRepository students,ClubApplicationRepository applications,
            ClubMembershipRepository memberships,CampusNotificationRepository notifications){
        this.clubs=clubs;this.students=students;this.applications=applications;this.memberships=memberships;this.notifications=notifications;
    }
    public List<Club> getAllClubs(){return clubs.findAll();}
    public Club getClubById(Long id){return clubs.findById(id).orElse(null);}
    public boolean deleteClub(Long id){if(!clubs.existsById(id))return false;clubs.deleteById(id);return true;}
    @Transactional(readOnly=true)
    public List<ClubResponse> directory(String search,String category,String viewer){
        String q=search==null?"":search.trim().toLowerCase(Locale.ROOT);String cat=category==null?"":category.trim();
        return clubs.findAll().stream().filter(c->cat.isBlank()||c.getCategory().equalsIgnoreCase(cat))
            .filter(c->q.isBlank()||String.join(" ",Objects.toString(c.getName(),""),Objects.toString(c.getCategory(),""),Objects.toString(c.getDescription(),""),Objects.toString(c.getActivities(),""))
                .toLowerCase(Locale.ROOT).contains(q)).map(c->view(c,viewer)).toList();
    }
    @Transactional(readOnly=true)
    public ClubResponse get(Long id,String viewer){Club c=clubs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));return view(c,viewer);}
    @Transactional(readOnly=true)
    public List<ApplicationResponse> mine(String email){return applications.findByStudentEmailIgnoreCaseOrderBySubmittedAtDesc(email).stream().map(ApplicationResponse::from).toList();}
    @Transactional
    public ApplicationResponse apply(Long clubId,ApplicationRequest request,String email){
        Student student=student(email);Club club=clubs.lockById(clubId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(memberships.existsByClubIdAndStudentIdAndActiveTrue(clubId,student.getId()))throw new ConflictException("You are already a member of this club.");
        if(applications.existsByClubIdAndStudentIdAndStatus(clubId,student.getId(),"PENDING"))throw new ConflictException("You already have a pending application for this club.");
        var previous=applications.findFirstByClubIdAndStudentIdOrderBySubmittedAtDesc(clubId,student.getId());
        if(previous.isPresent()&&"REJECTED".equals(previous.get().getStatus()))throw new ConflictException("This club does not accept reapplications after a rejection. You may apply again only after withdrawing a pending request.");
        var app=applications.save(new ClubApplication(club,student,request.motivation()==null?null:request.motivation().trim(),Instant.now()));
        return ApplicationResponse.from(app);
    }
    @Transactional
    public ApplicationResponse withdraw(Long id,String email){
        var existing=applications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        clubs.lockById(existing.getClub().getId()).orElseThrow();
        var app=applications.lockById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!app.getStudent().getEmail().equalsIgnoreCase(email))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        if(!"PENDING".equals(app.getStatus()))throw new ConflictException("Only pending applications can be withdrawn.");
        app.withdraw(app.getStudent(),Instant.now());return ApplicationResponse.from(applications.save(app));
    }
    @Transactional(readOnly=true)
    public List<ClubResponse> leaderClubs(String email){return clubs.findByLeaderEmailIgnoreCaseOrderByNameAsc(email).stream().map(c->view(c,email)).toList();}
    @Transactional(readOnly=true)
    public List<ApplicationResponse> clubApplications(Long clubId,String status,String email){
        Club club=clubs.findById(clubId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        requireLeader(club,email);
        String normalized=status==null||status.isBlank()?"ALL":status.toUpperCase(Locale.ROOT);
        if(!Set.of("ALL","PENDING","APPROVED","REJECTED","WITHDRAWN").contains(normalized))throw new IllegalArgumentException("Unknown application status.");
        List<ClubApplication> rows="ALL".equals(normalized)?applications.findByClubIdOrderBySubmittedAtDesc(clubId):applications.findByClubIdAndStatusOrderBySubmittedAtAsc(clubId,normalized);
        return rows.stream().map(ApplicationResponse::from).toList();
    }
    @Transactional
    public ApplicationResponse decide(Long applicationId,DecisionRequest request,String email){
        var found=applications.findById(applicationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Club club=clubs.lockById(found.getClub().getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Student leader=student(email);requireLeader(club,email);
        ClubApplication app=applications.lockById(applicationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(!"PENDING".equals(app.getStatus()))throw new ConflictException("Only pending applications may receive a decision.");
        String status=request.status().toUpperCase(Locale.ROOT);String note=request.decisionNote()==null?null:request.decisionNote().trim();
        app.decide(status,leader,note,Instant.now());applications.save(app);
        if("APPROVED".equals(status)){
            ClubMembership member=memberships.findByClubIdAndStudentId(club.getId(),app.getStudent().getId()).orElse(null);
            if(member==null)member=memberships.save(new ClubMembership(club,app.getStudent(),Instant.now()));else member.setActive(true);
            if(member.getId()!=null)memberships.save(member);
        }
        club.setMembersCount(Math.toIntExact(memberships.countByClubIdAndActiveTrue(club.getId())));clubs.save(club);
        notifications.save(new CampusNotification(app.getStudent(),"Club application "+status.toLowerCase(Locale.ROOT),
            "Your application to "+club.getName()+" was "+status.toLowerCase(Locale.ROOT)+"."+(note==null||note.isBlank()?"":" Note: "+note),
            "/student/clubs/applications",Instant.now()));
        return ApplicationResponse.from(app);
    }
    @Transactional
    public ClubResponse create(ClubRequest request){Club c=new Club();applyClub(c,request);return view(clubs.save(c),null);}
    @Transactional
    public ClubResponse update(Long id,ClubRequest request){Club c=clubs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));applyClub(c,request);return view(clubs.save(c),null);}
    @Transactional
    public void delete(Long id){Club c=clubs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(applications.countByClubId(id)>0||memberships.countByClubId(id)>0)throw new ConflictException("Clubs with application or membership history cannot be deleted.");clubs.delete(c);}

    private ClubResponse view(Club c,String viewer){
        Student s=viewer==null?null:students.findByEmailIgnoreCase(viewer).filter(x->"STUDENT".equals(x.getRole())).orElse(null);
        boolean member=s!=null&&memberships.existsByClubIdAndStudentIdAndActiveTrue(c.getId(),s.getId());
        String status=s==null?null:applications.findFirstByClubIdAndStudentIdOrderBySubmittedAtDesc(c.getId(),s.getId()).map(ClubApplication::getStatus).orElse(null);
        boolean canApply=s!=null&&!member&&!"PENDING".equals(status)&&!"REJECTED".equals(status);
        return ClubResponse.from(c,viewer,member,status,canApply,memberships.countByClubIdAndActiveTrue(c.getId()));
    }
    private Student student(String email){Student s=students.findByEmailIgnoreCase(email).orElseThrow(()->new AccessDeniedException("Student profile is unavailable."));if(!"STUDENT".equals(s.getRole()))throw new AccessDeniedException("A student account is required.");return s;}
    private void requireLeader(Club club,String email){if(club.getLeader()==null||!club.getLeader().getEmail().equalsIgnoreCase(email))throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
    private void applyClub(Club club,ClubRequest request){
        club.setName(request.name().trim());club.setCategory(request.category().trim());club.setDescription(request.description());
        club.setActivities(request.activities());club.setEligibility(request.eligibility());club.setDepartment(request.department());
        if(request.leaderEmail()==null||request.leaderEmail().isBlank()){club.setLeader(null);club.setPresident(null);}
        else{Student leader=student(request.leaderEmail().trim());club.setLeader(leader);club.setPresident(leader.getName());}
        club.setMembersCount(Math.toIntExact(memberships.countByClubIdAndActiveTrue(club.getId()==null?-1L:club.getId())));
    }
}
