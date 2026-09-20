package com.controller;

import com.entity.Club;
import com.service.ClubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    public List<Club> getAllClubs() {
        return clubService.getAllClubs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable String id) {

        Club club = clubService.getClubById(id);

        if (club == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(club);
    }

    @PostMapping
    public ResponseEntity<Club> createClub(@Valid @RequestBody Club club) {
        Club createdClub = clubService.createClub(club);
        return ResponseEntity
                .created(URI.create("/api/clubs/" + createdClub.getId()))
                .body(createdClub);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(
            @PathVariable String id,
            @Valid @RequestBody Club club) {

        Club updatedClub = clubService.updateClub(id, club);

        if (updatedClub == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedClub);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable String id) {

        return clubService.deleteClub(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}