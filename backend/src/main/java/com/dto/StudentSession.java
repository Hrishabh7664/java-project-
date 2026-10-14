package com.dto;
import java.time.LocalDate;
public record StudentSession(Long id, LocalDate date, String subjectCode, String subjectName,
                             Integer lectureNumber, String startTime, String endTime, boolean present) {}
