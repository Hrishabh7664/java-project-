package com.controller;

import com.dto.SubmissionDtos.*;
import com.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class SubmissionController {
    private final SubmissionService submissions;
    public SubmissionController(SubmissionService submissions) { this.submissions=submissions; }

    @PostMapping(value="/api/student/assignments/{assignmentId}/submission",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionResponse> submit(@PathVariable Long assignmentId,@RequestPart("file") MultipartFile file,Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissions.submit(assignmentId,file,auth));
    }
    @GetMapping("/api/student/assignments/{assignmentId}/submission")
    public SubmissionResponse mine(@PathVariable Long assignmentId,Authentication auth) { return submissions.mine(assignmentId,auth); }
    @GetMapping("/api/faculty/submissions")
    public List<SubmissionResponse> facultyList(@RequestParam(required=false) Long assignmentId,Authentication auth) {
        return submissions.facultySubmissions(assignmentId,auth);
    }
    @GetMapping("/api/faculty/assignments/{assignmentId}/submissions")
    public List<SubmissionResponse> forAssignment(@PathVariable Long assignmentId,Authentication auth) {
        return submissions.facultySubmissions(assignmentId,auth);
    }
    @PostMapping("/api/faculty/submissions/{submissionId}/review")
    public SubmissionResponse review(@PathVariable Long submissionId,@Valid @RequestBody ReviewRequest request,Authentication auth) {
        return submissions.review(submissionId,request,auth);
    }
    @GetMapping("/api/submissions/{submissionId}/versions/{version}/file")
    public ResponseEntity<Resource> file(@PathVariable Long submissionId,@PathVariable Integer version,Authentication auth) {
        var pdf=submissions.file(submissionId,version,auth);
        var disposition=ContentDisposition.inline().filename(pdf.filename(),StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).contentLength(pdf.size())
                .header(HttpHeaders.CONTENT_DISPOSITION,disposition.toString())
                .header("X-Content-Type-Options","nosniff").cacheControl(CacheControl.noStore())
                .body(new FileSystemResource(pdf.path()));
    }
}
