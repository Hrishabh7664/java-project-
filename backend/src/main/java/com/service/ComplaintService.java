package com.service;

import com.entity.Complaint;
import com.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    public Complaint createComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaint(Long id, Complaint details) {
        Complaint existing = complaintRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setCategory(details.getCategory());
        existing.setStatus(details.getStatus());
        existing.setSubmittedBy(details.getSubmittedBy());
        existing.setAnonymous(details.getAnonymous());

        return complaintRepository.save(existing);
    }

    public boolean deleteComplaint(Long id) {
        if (!complaintRepository.existsById(id)) {
            return false;
        }
        complaintRepository.deleteById(id);
        return true;
    }
}
