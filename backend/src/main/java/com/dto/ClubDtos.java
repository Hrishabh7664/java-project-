package com.dto;

import com.entity.Club;
import com.entity.ClubApplication;
import jakarta.validation.constraints.*;
import java.time.Instant;

public final class ClubDtos {
    private ClubDtos(){}
    public record ClubRequest(@NotBlank @Size(max=160) String name,@NotBlank @Size(max=60) String category,
        @Size(max=12000) String description,@Size(max=12000) String activities,@Size(max=3000) String eligibility,
        @Size(max=40) String department,String leaderEmail){}
    public record ApplicationRequest(@Size(max=3000) String motivation){}
    public record DecisionRequest(@NotBlank @Pattern(regexp="APPROVED|REJECTED") String status,@Size(max=3000) String decisionNote){}
    public record ClubResponse(Long id,String name,String category,String description,String activities,String eligibility,
        String department,String leaderName,String leaderEmail,boolean demoContent,long membersCount,
        boolean isLeader,boolean isMember,String latestApplicationStatus,boolean canApply) {
        public static ClubResponse from(Club c,String viewer,boolean member,String applicationStatus,boolean canApply,long members) {
            String leaderEmail=c.getLeader()==null?null:c.getLeader().getEmail();
            String leaderName=c.getLeader()==null?c.getPresident():c.getLeader().getName();
            return new ClubResponse(c.getId(),c.getName(),c.getCategory(),c.getDescription(),c.getActivities(),c.getEligibility(),
                c.getDepartment(),leaderName,leaderEmail,c.getDemoKey()!=null,members,
                viewer!=null&&viewer.equalsIgnoreCase(leaderEmail),member,applicationStatus,canApply);
        }
    }
    public record ApplicationResponse(Long id,Long clubId,String clubName,Long studentId,String studentName,String studentEmail,
        String department,String academicYear,String division,String batch,String motivation,String status,Instant submittedAt,
        String decidedByName,Instant decidedAt,String decisionNote) {
        public static ApplicationResponse from(ClubApplication a) {
            var s=a.getStudent();var d=a.getDecidedBy();
            return new ApplicationResponse(a.getId(),a.getClub().getId(),a.getClub().getName(),s.getId(),s.getName(),s.getEmail(),
                s.getDepartment(),s.getYear(),s.getDivision(),s.getBatch(),a.getMotivation(),a.getStatus(),a.getSubmittedAt(),
                d==null?null:d.getName(),a.getDecidedAt(),a.getDecisionNote());
        }
    }
    public record NotificationResponse(Long id,String title,String message,String link,Instant createdAt,boolean isRead) {}
}
