package com.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public record SyllabusRequest(@NotNull @Size(max=20000) String syllabus) {}
