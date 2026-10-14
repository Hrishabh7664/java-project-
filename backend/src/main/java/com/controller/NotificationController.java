package com.controller;
import com.dto.ClubDtos.NotificationResponse;
import com.repository.CampusNotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController
@RequestMapping("/api/student/notifications")
public class NotificationController {
    private final CampusNotificationRepository notifications;
    public NotificationController(CampusNotificationRepository notifications){this.notifications=notifications;}
    @GetMapping public List<NotificationResponse> mine(Authentication auth){return notifications.findByStudentEmailIgnoreCaseOrderByCreatedAtDesc(auth.getName()).stream().map(n->new NotificationResponse(n.getId(),n.getTitle(),n.getMessage(),n.getLink(),n.getCreatedAt(),n.isRead())).toList();}
    @PutMapping("/{id}/read") public NotificationResponse read(@PathVariable Long id,Authentication auth){
        var notification=notifications.findByIdAndStudentEmailIgnoreCase(id,auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        notification.setRead(true);notifications.save(notification);return new NotificationResponse(notification.getId(),notification.getTitle(),notification.getMessage(),notification.getLink(),notification.getCreatedAt(),true);
    }
}
