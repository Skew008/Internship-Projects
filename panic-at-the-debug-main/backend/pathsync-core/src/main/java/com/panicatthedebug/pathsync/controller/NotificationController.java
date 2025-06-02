package com.panicatthedebug.pathsync.controller;


import com.panicatthedebug.pathsync.model.WellnessNotification;
import com.panicatthedebug.pathsync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<WellnessNotification>> getPendingNotifications(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<WellnessNotification> notifications = notificationService.getPendingNotifications(userEmail);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{notificationId}/acknowledge")
    public ResponseEntity<Void> acknowledgeNotification(
            @PathVariable String notificationId,
            Authentication authentication) {
        notificationService.acknowledgeNotification(notificationId);
        return ResponseEntity.ok().build();
    }
}
