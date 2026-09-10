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

    public Club getClubById(String id) {
        return clubRepository.findById(id).orElse(null);
    }

    public Club createClub(Club club) {
        return clubRepository.save(club);
    }

    public Club updateClub(String id, Club club) {

        Club existingClub = clubRepository.findById(id).orElse(null);

        if (existingClub == null) {
            return null;
        }

        existingClub.setName(club.getName());
        existingClub.setCategory(club.getCategory());
        existingClub.setLogo(club.getLogo());
        existingClub.setCover(club.getCover());
        existingClub.setDescription(club.getDescription());
        existingClub.setMembersCount(club.getMembersCount());

        return clubRepository.save(existingClub);
    }

    public void deleteClub(String id) {
        clubRepository.deleteById(id);
    }
}