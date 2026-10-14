package com.controller;
import com.repository.ContentReportRepository;
import com.entity.ContentReport;
import com.service.PostService;
import com.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api/admin/moderation")
public class ModerationController {
 private final ContentReportRepository reports;private final PostService posts;private final CommentService comments;
 public ModerationController(ContentReportRepository reports,PostService posts,CommentService comments){this.reports=reports;this.posts=posts;this.comments=comments;}
 @GetMapping("/reports") public List<ContentReport> reports(){return reports.findAllByOrderByCreatedAtDesc();}
 @GetMapping("/content/{type}/{id}") public Object content(@PathVariable String type,@PathVariable Long id){
  Object value=switch(type.toUpperCase()){case "POST"->posts.getAnyPostById(id);case "COMMENT"->comments.getAnyCommentById(id);default->throw new IllegalArgumentException("Invalid content type");};
  if(value==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found");return value;
 }
 @PostMapping("/reports/{id}/resolve") @org.springframework.transaction.annotation.Transactional
 public ContentReport resolve(@PathVariable Long id,@RequestBody Map<String,String> req,org.springframework.security.core.Authentication auth){
  if(req==null)throw new IllegalArgumentException("Moderation decision details are required");
  ContentReport report=reports.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Report not found"));String action=req.getOrDefault("action","").toUpperCase();String reason=req.getOrDefault("reason","").trim();
  if(!List.of("HIDE","RESTORE","DISMISS").contains(action))throw new IllegalArgumentException("Action must be HIDE, RESTORE, or DISMISS");
  if(reason.isBlank()||reason.length()>1000)throw new IllegalArgumentException("A moderation reason is required");
  if(!action.equals("DISMISS")){
   boolean hidden=action.equals("HIDE");
   if(report.getContentType().equals("POST")){
    if(posts.moderatePost(report.getContentId(),hidden,auth.getName(),reason)==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Post not found");
   }else if(report.getContentType().equals("COMMENT")){
    if(comments.moderateComment(report.getContentId(),hidden,auth.getName(),reason)==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Comment not found");
   }else throw new IllegalArgumentException("Invalid report content type");
  }
  report.setStatus(action.equals("DISMISS")?"DISMISSED":"RESOLVED");report.setModerationReason(reason);report.setResolvedBy(auth.getName());report.setResolvedAt(Instant.now());return reports.save(report);
 }
}
