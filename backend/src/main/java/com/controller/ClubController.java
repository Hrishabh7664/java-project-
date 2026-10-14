package com.controller;

import com.dto.ClubDtos.*;
import com.service.ClubService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    public List<ClubResponse> getAllClubs(@RequestParam(required=false) String search,@RequestParam(required=false) String category,org.springframework.security.core.Authentication auth) {
        return clubService.directory(search,category,auth.getName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> getClubById(@PathVariable Long id,org.springframework.security.core.Authentication auth) {
        return ResponseEntity.ok(clubService.get(id,auth.getName()));
    }

    @PostMapping
    public ResponseEntity<ClubResponse> createClub(@Valid @RequestBody ClubRequest club) {
        ClubResponse createdClub = clubService.create(club);
        return ResponseEntity
                .created(URI.create("/api/clubs/" + createdClub.id()))
                .body(createdClub);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubResponse> updateClub(@PathVariable Long id, @Valid @RequestBody ClubRequest club) {
        return ResponseEntity.ok(clubService.update(id,club));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.delete(id);return ResponseEntity.noContent().build();
    }
}
