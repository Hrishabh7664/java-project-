package com.controller;

import com.entity.Election;
import com.service.ElectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/elections")
@CrossOrigin(origins = "*")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public List<Election> getAllElections() {
        return electionService.getAllElections();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable Long id) {
        Election election = electionService.getElectionById(id);
        if (election == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(election);
    }

    @PostMapping
    public ResponseEntity<Election> createElection(@Valid @RequestBody Election election) {
        Election created = electionService.createElection(election);
        return ResponseEntity
                .created(URI.create("/api/elections/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Election> updateElection(@PathVariable Long id, @Valid @RequestBody Election election) {
        Election updated = electionService.updateElection(id, election);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        return electionService.deleteElection(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
