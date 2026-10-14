package com.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record AssignmentRequest(
    @NotBlank @Size(max=180) String title,
    @NotBlank @Size(max=12000) String description,
    @NotNull Long subjectId,
    @NotNull Long classId,
    @NotNull LocalDateTime deadlineAt,
    @NotNull @DecimalMin("0.01") @DecimalMax("10000.00") BigDecimal maximumMarks,
    @NotBlank @Pattern(regexp="DRAFT|PUBLISHED") String status,
    Boolean allowResubmission,
    Boolean allowLateSubmission
) {}
