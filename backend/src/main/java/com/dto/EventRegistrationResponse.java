package com.dto;

import com.entity.Event;
import com.entity.EventRegistration;
import java.time.Instant;

public record EventRegistrationResponse(Long id, EventSummary event, Instant registeredAt) {
    public static EventRegistrationResponse from(EventRegistration registration, long registrations) {
        Event event = registration.getEvent();
        return new EventRegistrationResponse(registration.getId(),
                new EventSummary(event.getId(), event.getTitle(), event.getDate(), event.getTime(),
                        event.getLocation(), event.getOrganizer(), event.getCategory(), event.getCapacity(),
                        event.getRegistrationDeadline(), event.getEligibility(), registrations), registration.getRegisteredAt());
    }
    public record EventSummary(Long id, String title, String date, String time, String location,
                               String organizer, String category, Integer capacity, String registrationDeadline,
                               String eligibility, long registrations) {}
}
