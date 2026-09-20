package com.service;

import com.entity.Assignment;
import com.repository.AssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElse(null);
    }

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Long id, Assignment details) {
        Assignment existing = assignmentRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setSubject(details.getSubject());
        existing.setFacultyName(details.getFacultyName());
        existing.setDueDate(details.getDueDate());
        existing.setStatus(details.getStatus());

        return assignmentRepository.save(existing);
    }

    public boolean deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            return false;
        }
        assignmentRepository.deleteById(id);
        return true;
    }
}
