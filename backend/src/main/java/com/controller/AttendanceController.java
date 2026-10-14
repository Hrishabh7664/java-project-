package com.controller;

import com.dto.AttendanceDtos.*;
import com.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
public class AttendanceController {
    private final AttendanceService attendance;
    public AttendanceController(AttendanceService attendance) { this.attendance = attendance; }
    @GetMapping("/api/faculty/attendance/options")
    public List<AssignmentOption> options(Authentication auth) { return attendance.options(auth.getName()); }
    @GetMapping("/api/faculty/attendance/roster")
    public RosterResponse roster(@RequestParam Long assignmentId, @RequestParam LocalDate date, Authentication auth) {
        return attendance.roster(assignmentId, date, auth.getName());
    }
    @GetMapping("/api/faculty/attendance/sessions")
    public List<SessionResponse> history(Authentication auth) { return attendance.history(auth.getName()); }
    @PostMapping("/api/faculty/attendance/sessions")
    public ResponseEntity<SessionResponse> submit(@Valid @RequestBody SaveRequest request, Authentication auth) {
        return ResponseEntity.status(201).body(attendance.submit(request, auth.getName()));
    }
    @PutMapping("/api/faculty/attendance/sessions/{id}")
    public SessionResponse correct(@PathVariable Long id, @Valid @RequestBody SaveRequest request, Authentication auth) {
        return attendance.correct(id, request, auth.getName());
    }
    @GetMapping("/api/student/attendance")
    public StudentSummary mine(Authentication auth) { return attendance.ownAttendance(auth.getName()); }
}
