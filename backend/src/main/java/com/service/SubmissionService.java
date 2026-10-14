package com.service;

import com.dto.SubmissionDtos.*;
import com.entity.*;
import com.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
public class SubmissionService {
    private final AssignmentRepository assignments;
    private final AssignmentSubmissionRepository submissions;
    private final SubmissionVersionRepository versions;
    private final SubmissionReviewRepository reviews;
    private final StudentRepository students;
    private final StudentEnrollmentRepository enrollments;
    private final AssignmentFileStorage storage;
    public SubmissionService(AssignmentRepository assignments,AssignmentSubmissionRepository submissions,
            SubmissionVersionRepository versions,SubmissionReviewRepository reviews,StudentRepository students,
            StudentEnrollmentRepository enrollments,AssignmentFileStorage storage) {
        this.assignments=assignments;this.submissions=submissions;this.versions=versions;this.reviews=reviews;
        this.students=students;this.enrollments=enrollments;this.storage=storage;
    }

    @Transactional
    public SubmissionResponse submit(Long assignmentId,MultipartFile file,Authentication auth) {
        Student student=student(auth);
        storage.validate(file);
        Assignment assignment=assignments.findByIdForUpdate(assignmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"PUBLISHED".equals(assignment.getStatus()) || !enrolled(student,assignment))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var now=AssignmentService.collegeNow();
        boolean late=now.isAfter(assignment.getDeadlineAt());
        if (late && !assignment.isAllowLateSubmission()) throw new IllegalArgumentException("The assignment deadline has passed.");
        AssignmentSubmission submission=submissions.findByAssignmentIdAndStudentId(assignmentId,student.getId()).orElse(null);
        if (submission!=null && (!"RESUBMISSION_REQUESTED".equals(submission.getStatus()) || !assignment.isAllowResubmission()))
            throw new ConflictException("A submission already exists. Resubmission is available only when faculty requests it.");
        AssignmentFileStorage.StoredFile stored=storage.store(file);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) storage.delete(stored.key());
            }
        });
        try {
            if (submission==null) {
                submission=submissions.save(new AssignmentSubmission(assignment,student,Instant.now(),late));
            } else {
                submission.setStatus("SUBMITTED");submission.setCurrentVersion(submission.getCurrentVersion()+1);
                submission.setSubmittedAt(Instant.now());submission.setLate(late);submission.setMarks(null);
                submission.setFeedback(null);submission.setReviewerEmail(null);submission.setReviewedAt(null);
                submission=submissions.save(submission);
            }
            versions.save(new SubmissionVersion(submission,submission.getCurrentVersion(),stored.key(),stored.originalFilename(),stored.size()));
            return response(submission);
        } catch (RuntimeException ex) {
            storage.delete(stored.key());
            throw ex;
        }
    }

    @Transactional(readOnly=true)
    public SubmissionResponse mine(Long assignmentId,Authentication auth) {
        Student student=student(auth);
        Assignment assignment=assignments.findById(assignmentId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!enrolled(student,assignment)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        AssignmentSubmission sub=submissions.findByAssignmentIdAndStudentId(assignmentId,student.getId())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return response(sub);
    }

    @Transactional(readOnly=true)
    public List<SubmissionResponse> facultySubmissions(Long assignmentId,Authentication auth) {
        List<AssignmentSubmission> found;
        if (assignmentId!=null) {
            assignments.findByIdAndFacultyEmail(assignmentId,auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            found=submissions.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId);
        } else found=submissions.findByAssignmentFacultyEmailOrderBySubmittedAtDesc(auth.getName());
        return found.stream().map(this::response).toList();
    }

    @Transactional
    public SubmissionResponse review(Long submissionId,ReviewRequest request,Authentication auth) {
        AssignmentSubmission sub=submissions.findById(submissionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        assignments.findByIdAndFacultyEmail(sub.getAssignment().getId(),auth.getName())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        String action=request.action()==null?"":request.action().trim().toUpperCase();
        if (!List.of("GRADE","APPROVE","REQUEST_RESUBMISSION","REJECT").contains(action))
            throw new IllegalArgumentException("Choose GRADE, APPROVE, REQUEST_RESUBMISSION, or REJECT.");
        if (!List.of("SUBMITTED","GRADED").contains(sub.getStatus()))
            throw new IllegalArgumentException("This submission is no longer available for review.");
        BigDecimal marks=request.marks()==null?sub.getMarks():request.marks();
        if (List.of("GRADE","APPROVE").contains(action) && marks==null)
            throw new IllegalArgumentException("Marks are required to grade or approve a submission.");
        if (marks!=null && (marks.signum()<0 || marks.compareTo(sub.getAssignment().getMaximumMarks())>0))
            throw new IllegalArgumentException("Marks must be between 0 and the assignment maximum.");
        if (List.of("REQUEST_RESUBMISSION","REJECT").contains(action) && (request.feedback()==null || request.feedback().isBlank()))
            throw new IllegalArgumentException("Written feedback is required for this review action.");
        if ("REQUEST_RESUBMISSION".equals(action) && !sub.getAssignment().isAllowResubmission())
            throw new IllegalArgumentException("This assignment does not allow resubmission.");
        String newStatus=switch(action) {
            case "GRADE" -> "GRADED";
            case "APPROVE" -> "APPROVED";
            case "REQUEST_RESUBMISSION" -> "RESUBMISSION_REQUESTED";
            default -> "REJECTED";
        };
        sub.setStatus(newStatus);sub.setMarks(marks);sub.setFeedback(request.feedback());
        sub.setReviewerEmail(auth.getName());sub.setReviewedAt(Instant.now());
        reviews.save(new SubmissionReview(sub,auth.getName(),action,marks,request.feedback()));
        return response(submissions.save(sub));
    }

    @Transactional(readOnly=true)
    public PdfFile file(Long submissionId,Integer versionNumber,Authentication auth) {
        AssignmentSubmission sub=submissions.findById(submissionId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Student account=students.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN));
        if ("STUDENT".equals(account.getRole())) {
            if (!sub.getStudent().getId().equals(account.getId())) throw new AccessDeniedException("This submission belongs to another student.");
        } else if ("FACULTY".equals(account.getRole())) {
            if (!auth.getName().equalsIgnoreCase(sub.getAssignment().getFaculty()==null?"":sub.getAssignment().getFaculty().getEmail()))
                throw new AccessDeniedException("This submission belongs to another faculty member's class.");
        } else if (!"ADMIN".equals(account.getRole())) throw new AccessDeniedException("Access to this submission is not allowed.");
        SubmissionVersion version=versions.findBySubmissionIdAndVersionNumber(submissionId,versionNumber)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        Path path=storage.resolve(version.getFileKey());
        if (!Files.isRegularFile(path)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Stored PDF is missing.");
        return new PdfFile(path,version.getOriginalFilename(),version.getFileSize());
    }

    private Student student(Authentication auth) {
        Student s=students.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new AccessDeniedException("Student account not found."));
        if (!"STUDENT".equals(s.getRole())) throw new AccessDeniedException("A student account is required.");
        return s;
    }
    private boolean enrolled(Student student,Assignment assignment) {
        return assignment.getTargetAcademicClass()!=null && enrollments.findByStudentEmailIgnoreCaseAndActiveTrue(student.getEmail()).stream()
                .anyMatch(e->e.getAcademicClass().getId().equals(assignment.getTargetAcademicClass().getId()));
    }
    private SubmissionResponse response(AssignmentSubmission s) {
        return SubmissionResponse.from(s,versions.findBySubmissionIdOrderByVersionNumberAsc(s.getId()));
    }
    public record PdfFile(Path path,String filename,long size) {}
}
