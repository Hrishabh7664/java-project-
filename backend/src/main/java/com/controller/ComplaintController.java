package com.controller;

import com.entity.Complaint;
import com.service.ComplaintService;
import com.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final StudentRepository studentRepository;

    public ComplaintController(ComplaintService complaintService, StudentRepository studentRepository) {
        this.complaintService = complaintService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Complaint> getAllComplaints(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return complaintService.getAllComplaints().stream()
                .filter(item -> admin || authentication.getName().equalsIgnoreCase(item.getSubmittedByEmail())).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id, Authentication authentication) {
        Complaint complaint = complaintService.getComplaintById(id);
        if (complaint == null) {
            return ResponseEntity.notFound().build();
        }
        boolean admin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!admin && !authentication.getName().equalsIgnoreCase(complaint.getSubmittedByEmail())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(complaint);
    }

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody Complaint complaint, Authentication authentication) {
        complaint.setSubmittedByEmail(authentication.getName());
        complaint.setSubmittedBy(Boolean.TRUE.equals(complaint.getAnonymous()) ? "Anonymous"
                : studentRepository.findByEmailIgnoreCase(authentication.getName())
                .map(student -> student.getName()).orElse(authentication.getName()));
        complaint.setStatus("SUBMITTED");
        Complaint created = complaintService.createComplaint(complaint);
        return ResponseEntity
                .created(URI.create("/api/complaints/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @Valid @RequestBody Complaint complaint, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return ResponseEntity.status(403).build();
        Complaint updated = complaintService.updateComplaint(id, complaint);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id, Authentication authentication) {
        if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            return ResponseEntity.status(403).build();
        return complaintService.deleteComplaint(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
