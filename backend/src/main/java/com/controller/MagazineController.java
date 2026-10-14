package com.controller;
import com.entity.MagazineIssue;import com.repository.MagazineIssueRepository;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;import java.util.List;import java.util.Map;
@RestController @RequestMapping("/api/magazine") public class MagazineController{
 private final MagazineIssueRepository issues;public MagazineController(MagazineIssueRepository r){issues=r;}
 @GetMapping("/issues") public List<MagazineIssue> publicIssues(){return issues.findByPublishedTrueOrderByPublicationDateDesc();}
 @GetMapping("/issues/{id}") public MagazineIssue issue(@PathVariable Long id){return issues.findById(id).filter(MagazineIssue::isPublished).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Issue not found"));}
 @GetMapping("/admin/issues") public List<MagazineIssue> adminIssues(){return issues.findAll();}
 @PostMapping("/admin/issues") public ResponseEntity<MagazineIssue> create(@RequestBody MagazineIssue issue){issue.setPublished(false);issue.setId(null);return ResponseEntity.status(201).body(issues.save(issue));}
 @PutMapping("/admin/issues/{id}/publish") public MagazineIssue publish(@PathVariable Long id,@RequestBody Map<String,Boolean> body){MagazineIssue issue=issues.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));issue.setPublished(Boolean.TRUE.equals(body.get("published")));return issues.save(issue);}
}
