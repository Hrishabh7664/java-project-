package com.controller;

import com.dto.ClubDtos.*;
import com.service.ClubService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class ClubApplicationController {
    private final ClubService clubs;
    public ClubApplicationController(ClubService clubs){this.clubs=clubs;}
    @GetMapping("/api/student/clubs/applications") public List<ApplicationResponse> mine(Authentication auth){return clubs.mine(auth.getName());}
    @PostMapping("/api/student/clubs/{clubId}/applications") public ApplicationResponse apply(@PathVariable Long clubId,@Valid @RequestBody ApplicationRequest request,Authentication auth){return clubs.apply(clubId,request,auth.getName());}
    @DeleteMapping("/api/student/clubs/applications/{applicationId}") public ApplicationResponse withdraw(@PathVariable Long applicationId,Authentication auth){return clubs.withdraw(applicationId,auth.getName());}
    @GetMapping("/api/student/clubs/leader") public List<ClubResponse> leaderClubs(Authentication auth){return clubs.leaderClubs(auth.getName());}
    @GetMapping("/api/student/clubs/leader/{clubId}/applications") public List<ApplicationResponse> clubApplications(@PathVariable Long clubId,@RequestParam(required=false) String status,Authentication auth){return clubs.clubApplications(clubId,status,auth.getName());}
    @PutMapping("/api/student/clubs/leader/applications/{applicationId}") public ApplicationResponse decide(@PathVariable Long applicationId,@Valid @RequestBody DecisionRequest request,Authentication auth){return clubs.decide(applicationId,request,auth.getName());}
}
