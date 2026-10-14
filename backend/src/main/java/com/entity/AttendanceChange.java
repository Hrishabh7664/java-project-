package com.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "attendance_changes")
public class AttendanceChange {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "record_id", nullable = false) private AttendanceRecord record;
    @Column(name = "changed_by", nullable = false) private String changedBy;
    @Column(name = "previous_status", nullable = false) private boolean previousStatus;
    @Column(name = "new_status", nullable = false) private boolean newStatus;
    @Column(name = "changed_at", nullable = false, updatable = false) private Instant changedAt = Instant.now();
    protected AttendanceChange() {}
    public AttendanceChange(AttendanceRecord record, String changedBy, boolean previousStatus, boolean newStatus) {
        this.record = record; this.changedBy = changedBy; this.previousStatus = previousStatus; this.newStatus = newStatus;
    }
}
