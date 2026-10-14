package com.controller;

import com.dto.AssignmentRequest;
import com.dto.AssignmentResponse;
import com.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    public AssignmentController(AssignmentService assignmentService) { this.assignmentService=assignmentService; }
    @GetMapping public List<AssignmentResponse> getAll(Authentication auth) { return assignmentService.getAll(auth); }
    @GetMapping("/{id}") public ResponseEntity<AssignmentResponse> getById(@PathVariable Long id,Authentication auth) {
        return ResponseEntity.ok(assignmentService.get(id,auth));
    }
    @PostMapping public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest request,Authentication auth) {
        AssignmentResponse created=assignmentService.create(request,auth);
        return ResponseEntity.created(URI.create("/api/assignments/"+created.id())).body(created);
    }
    @PutMapping("/{id}") public ResponseEntity<AssignmentResponse> update(@PathVariable Long id,
        @Valid @RequestBody AssignmentRequest request,Authentication auth) {
        return ResponseEntity.ok(assignmentService.update(id,request,auth));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication auth) {
        assignmentService.delete(id,auth); return ResponseEntity.noContent().build();
    }
}
