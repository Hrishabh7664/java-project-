package com.service;

import com.entity.Event;
import com.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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

        return eventRepository.save(existing);
    }

    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }
        eventRepository.deleteById(id);
        return true;
    }
}
