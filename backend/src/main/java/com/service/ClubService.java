package com.service;

import com.entity.Club;
import com.repository.ClubRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getClubById(Long id) {
        return clubRepository.findById(id).orElse(null);
    }

    public Club createClub(Club club) {
        return clubRepository.save(club);
    }

    public Club updateClub(Long id, Club clubDetails) {
        Club existingClub = clubRepository.findById(id).orElse(null);
        if (existingClub == null) {
            return null;
        }

        existingClub.setName(clubDetails.getName());
        existingClub.setCategory(clubDetails.getCategory());
        existingClub.setLogo(clubDetails.getLogo());
        existingClub.setCover(clubDetails.getCover());
        existingClub.setDescription(clubDetails.getDescription());
        existingClub.setDepartment(clubDetails.getDepartment());
        existingClub.setPresident(clubDetails.getPresident());
        existingClub.setMembersCount(clubDetails.getMembersCount());

        return clubRepository.save(existingClub);
    }

    public boolean deleteClub(Long id) {
        if (!clubRepository.existsById(id)) {
            return false;
        }
        clubRepository.deleteById(id);
        return true;
    }
}