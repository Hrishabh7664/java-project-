package com.service;

import com.entity.Event;
import com.repository.EventRepository;
import com.entity.EventRegistration;
import com.repository.EventRegistrationRepository;
import com.repository.StudentRepository;
import com.dto.EventRegistrationResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final StudentRepository studentRepository;

    public EventService(EventRepository eventRepository, EventRegistrationRepository registrationRepository, StudentRepository studentRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.studentRepository = studentRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event existing = eventRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(eventDetails.getTitle());
        existing.setDescription(eventDetails.getDescription());
        existing.setOrganizer(eventDetails.getOrganizer());
        existing.setDate(eventDetails.getDate());
        existing.setTime(eventDetails.getTime());
        existing.setLocation(eventDetails.getLocation());
        existing.setCategory(eventDetails.getCategory());
        existing.setCapacity(eventDetails.getCapacity());
        existing.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
        existing.setEligibility(eventDetails.getEligibility());

        return eventRepository.save(existing);
    }

    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }
        eventRepository.deleteById(id);
        return true;
    }

    @Transactional
    public EventRegistrationResponse register(Long eventId, Authentication authentication) {
        var event = eventRepository.findByIdForUpdate(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        var student = studentRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student account not found"));
        if (!"STUDENT".equals(student.getRole())) throw new AccessDeniedException("Only students can register for events");
        if (registrationRepository.existsByEventIdAndStudentId(eventId, student.getId()))
            throw new IllegalArgumentException("You are already registered for this event");
        if (event.getRegistrationDeadline() != null && !event.getRegistrationDeadline().isBlank()
                && LocalDate.now().isAfter(LocalDate.parse(event.getRegistrationDeadline())))
            throw new IllegalArgumentException("The registration deadline has passed");
        long count = registrationRepository.countByEventId(eventId);
        if (event.getCapacity() != null && event.getCapacity() >= 0 && count >= event.getCapacity())
            throw new IllegalArgumentException("This event has reached capacity");
        if (event.getEligibility() != null && !event.getEligibility().isBlank()
                && !event.getEligibility().equalsIgnoreCase("All")
                && !event.getEligibility().equalsIgnoreCase(student.getDepartment())
                && !event.getEligibility().equalsIgnoreCase(student.getYear()))
            throw new AccessDeniedException("You are not eligible to register for this event");
        var registration = registrationRepository.save(new EventRegistration(event, student));
        return EventRegistrationResponse.from(registration, count + 1);
    }

    @Transactional
    public void cancel(Long eventId, Authentication authentication) {
        var event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        var student = studentRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student account not found"));
        if (!"STUDENT".equals(student.getRole())) throw new AccessDeniedException("Only students can cancel event registrations");
        if (event.getRegistrationDeadline() != null && !event.getRegistrationDeadline().isBlank()
                && LocalDate.now().isAfter(LocalDate.parse(event.getRegistrationDeadline())))
            throw new IllegalArgumentException("The registration deadline has passed");
        if (!registrationRepository.existsByEventIdAndStudentId(eventId, student.getId()))
            throw new IllegalArgumentException("You are not registered for this event");
        registrationRepository.deleteByEventIdAndStudentId(eventId, student.getId());
    }

    @Transactional(readOnly = true)
    public List<EventRegistrationResponse> myRegistrations(Authentication authentication) {
        var student = studentRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Student account not found"));
        return registrationRepository.findByStudentIdOrderByRegisteredAtDesc(student.getId()).stream()
                .map(r -> EventRegistrationResponse.from(r, registrationRepository.countByEventId(r.getEvent().getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public long registrationCount(Long eventId) {
        return registrationRepository.countByEventId(eventId);
    }
}
