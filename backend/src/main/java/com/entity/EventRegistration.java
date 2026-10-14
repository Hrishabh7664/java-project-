package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "event_registrations",
        uniqueConstraints = @UniqueConstraint(name = "uk_event_registration_student_event", columnNames = {"event_id", "student_id"}))
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, updatable = false)
    private Instant registeredAt = Instant.now();

    protected EventRegistration() {}

    public EventRegistration(Event event, Student student) {
        this.event = event;
        this.student = student;
    }

    public Long getId() { return id; }
    public Event getEvent() { return event; }
    public Student getStudent() { return student; }
    public Instant getRegisteredAt() { return registeredAt; }
}
