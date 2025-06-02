package com.panicatthedebug.pathsync.controller;

import com.panicatthedebug.pathsync.model.ActiveSession;
import com.panicatthedebug.pathsync.service.ActivityTrackingService;
import com.panicatthedebug.pathsync.service.WellbeingMonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityTrackingService activityService;
    @Autowired
    private WellbeingMonitoringService monitoringService;

    @PostMapping("/heartbeat")
    public ResponseEntity<Void> recordHeartbeat(Authentication authentication) {
        String userEmail = authentication.getName();

        activityService.recordActivity(userEmail);

        monitoringService.checkUserForBreakNotification(userEmail);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/end-session")
    public ResponseEntity<Void> endSession(Authentication authentication) {
        String userEmail = authentication.getName();
        activityService.endSession(userEmail);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/session")
    public ResponseEntity<ActiveSession> getCurrentSession(Authentication authentication) {
        String userEmail = authentication.getName();
        return activityService.sessionRepository.findByUserEmailAndIsActiveTrue(userEmail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
