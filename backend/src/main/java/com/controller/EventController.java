package com.controller;

import com.entity.Event;
import com.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import com.dto.EventRegistrationResponse;
import com.dto.EventRegistrantResponse;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/shared") public List<com.entity.EventShare> shared(){return eventService.getActiveShares();}

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{id}/registrations/count")
    public long registrationCount(@PathVariable Long id) { return eventService.registrationCount(id); }

    @GetMapping("/{id}/registrations")
    public List<EventRegistrantResponse> getRegistrants(@PathVariable Long id) {
        return eventService.getRegistrants(id);
    }

    @PostMapping("/{id}/registrations")
    public ResponseEntity<EventRegistrationResponse> register(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.status(201).body(eventService.register(id, authentication));
    }

    @DeleteMapping("/{id}/registrations/me")
    public ResponseEntity<Void> cancel(@PathVariable Long id, Authentication authentication) {
        eventService.cancel(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations/me")
    public List<EventRegistrationResponse> myRegistrations(Authentication authentication) {
        return eventService.myRegistrations(authentication);
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity
                .created(URI.create("/api/events/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        Event updated = eventService.updateEvent(id, event);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
