package com.controller;
import com.entity.ContentReport;
import com.repository.ContentReportRepository;
import com.repository.PostRepository;
import com.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController @RequestMapping("/api/content-reports")
public class ContentReportController {
 private final ContentReportRepository reports;private final PostRepository posts;private final CommentRepository comments;
 public ContentReportController(ContentReportRepository reports,PostRepository posts,CommentRepository comments){this.reports=reports;this.posts=posts;this.comments=comments;}
 @PostMapping @org.springframework.transaction.annotation.Transactional
 public ResponseEntity<ContentReport> create(@RequestBody Map<String,String> body,Authentication auth){
  if(body==null)throw new IllegalArgumentException("Report details are required");
  String type=body.getOrDefault("contentType","").toUpperCase();Long id;
  try{id=Long.valueOf(body.getOrDefault("contentId",""));}catch(Exception e){throw new IllegalArgumentException("A valid contentId is required");}
  String reason=body.getOrDefault("reason","").trim();if(!type.equals("POST")&&!type.equals("COMMENT"))throw new IllegalArgumentException("Content type must be POST or COMMENT");
  if(id<1)throw new IllegalArgumentException("A valid contentId is required");
  if(reason.isBlank()||reason.length()>1000)throw new IllegalArgumentException("A report reason (up to 1000 characters) is required");
  boolean exists=type.equals("POST")?posts.findById(id).filter(p->!p.isHidden()).isPresent():comments.findById(id).filter(c->!c.isHidden()).isPresent();
  if(!exists)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Content not found");
  if(reports.countByReporterEmailIgnoreCaseAndContentTypeAndContentId(auth.getName(),type,id)>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already reported this content");
  try { return ResponseEntity.status(HttpStatus.CREATED).body(reports.saveAndFlush(new ContentReport(type,id,auth.getName(),reason))); }
  catch(org.springframework.dao.DataIntegrityViolationException duplicate){throw new ResponseStatusException(HttpStatus.CONFLICT,"You have already reported this content");}
 }
}
