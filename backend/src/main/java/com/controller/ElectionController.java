package com.controller;

import com.entity.Election;
import com.entity.ElectionCandidate;
import com.service.ElectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import com.dto.ElectionDtos.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<ElectionResponse> getAllElections(Authentication auth) {
        return electionService.list(auth.getName(),isAdmin(auth));
    }

    @GetMapping("/{id}")
    public ElectionResponse getElectionById(@PathVariable Long id,Authentication auth) {
        return electionService.get(id,auth.getName(),isAdmin(auth));
    }

    @PostMapping
    public ResponseEntity<Election> createElection(@Valid @RequestBody Election election,Authentication auth) {
        Election created = electionService.create(election,auth.getName());
        return ResponseEntity
                .created(URI.create("/api/elections/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Election> updateElection(@PathVariable Long id, @Valid @RequestBody Election election,Authentication auth) {
        Election updated = electionService.update(id, election,auth.getName());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id,Authentication auth) {
        return electionService.deleteDraft(id,auth.getName())
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(409).build();
    }

    @PostMapping("/{id}/candidates") public ResponseEntity<?> addCandidate(@PathVariable Long id,@RequestBody CandidateRequest req,Authentication auth){return ResponseEntity.status(201).body(electionService.addCandidate(id,req,auth.getName()));}
    @PutMapping("/{id}/candidates/{candidateId}") public ElectionCandidate candidateStatus(@PathVariable Long id,@PathVariable Long candidateId,@RequestBody java.util.Map<String,Boolean> body,Authentication auth){return electionService.updateCandidateStatus(id,candidateId,Boolean.TRUE.equals(body.get("approved")),auth.getName());}
    @PutMapping("/{id}/lifecycle") public Election lifecycle(@PathVariable Long id,@RequestBody LifecycleRequest req,Authentication auth){return electionService.lifecycle(id,req,auth.getName());}
    @PostMapping("/{id}/ballots") public ResponseEntity<Void> vote(@PathVariable Long id,@RequestBody BallotRequest req,Authentication auth){electionService.vote(id,req.candidateId(),auth.getName());return ResponseEntity.status(201).build();}
    @GetMapping("/{id}/results") public ResultsResponse results(@PathVariable Long id){return electionService.results(id);}
    @GetMapping("/{id}/history") public List<com.entity.ElectionAudit> history(@PathVariable Long id){return electionService.history(id);}
    @GetMapping("/{id}/status") public java.util.Map<String,Boolean> status(@PathVariable Long id,Authentication auth){return java.util.Map.of("eligible",electionService.get(id,auth.getName(),false).eligible(),"voted",electionService.hasVoted(id,auth.getName()));}
    private boolean isAdmin(Authentication auth){return auth.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_ADMIN"));}
}
