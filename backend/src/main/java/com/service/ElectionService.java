package com.service;

import com.entity.Election;
import com.repository.ElectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public Election getElectionById(Long id) {
        return electionRepository.findById(id).orElse(null);
    }

    public Election createElection(Election election) {
        return electionRepository.save(election);
    }

    public Election updateElection(Long id, Election details) {
        Election existing = electionRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setTitle(details.getTitle());
        existing.setPosition(details.getPosition());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setStatus(details.getStatus());

        return electionRepository.save(existing);
    }

    public boolean deleteElection(Long id) {
        if (!electionRepository.existsById(id)) {
            return false;
        }
        electionRepository.deleteById(id);
        return true;
    }
}
