package com.dto;
import java.util.List;
public final class ElectionDtos {
 private ElectionDtos(){}
 public record CandidateRequest(String name,String department,String year,String manifesto){}
 public record CandidateResponse(Long id,String name,String department,String year,String manifesto,long votes,boolean approved){}
 public record ElectionResponse(Long id,String title,String position,String description,String startDate,String endDate,String status,String eligibleDepartments,String eligibleYears,boolean eligible,boolean userHasVoted,boolean resultsPublished,long participation,List<CandidateResponse> candidates){}
 public record BallotRequest(Long candidateId){}
 public record ResultsResponse(Long electionId,String title,List<CandidateResponse> results,long ballots){}
 public record LifecycleRequest(String status,Boolean publishResults){}
}
