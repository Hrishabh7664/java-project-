package com.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record ScheduleRequest(
    @NotBlank @Pattern(regexp="MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY") String dayOfWeek,
    @NotBlank @Pattern(regexp="([01]\\d|2[0-3]):[0-5]\\d") String startTime,
    @NotBlank @Pattern(regexp="([01]\\d|2[0-3]):[0-5]\\d") String endTime,
    @NotBlank @Size(max=120) String classroom
) {}
