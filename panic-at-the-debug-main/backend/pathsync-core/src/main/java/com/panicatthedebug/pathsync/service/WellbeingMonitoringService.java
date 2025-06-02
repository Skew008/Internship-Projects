package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.model.ActiveSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class WellbeingMonitoringService {

    @Autowired
    private ActivityTrackingService activityService;
    @Autowired
    private NotificationService notificationService;

    @Value("${wellbeing.session.maxActiveMinutes}")
    private int maxActiveMinutesBeforeBreak;

    @Value("${wellbeing.notification.reminderInterval}")
    private int notificationReminderIntervalMinutes;

//    @Scheduled(fixedRate = 60000)
//    public void checkActiveSessions() {
//        // This would need to scan all active sessions in a production system
//        // For simplicity in this example, we'd handle this when activity is recorded
//    }

    public void checkUserForBreakNotification(String userEmail) {
        int activeMinutes = activityService.getCurrentActiveTimeMinutes(userEmail);

        if (activeMinutes >= maxActiveMinutesBeforeBreak) {
            boolean alreadyHasBreakNotification = activityService.sessionRepository
                    .findByUserEmailAndIsActiveTrue(userEmail)
                    .map(ActiveSession::isBreakNotificationSent)
                    .orElse(false);

            boolean hasRecentNotification = notificationService.hasRecentBreakNotification(
                    userEmail, notificationReminderIntervalMinutes);

            if (!alreadyHasBreakNotification || !hasRecentNotification) {
                notificationService.createBreakReminder(userEmail, activeMinutes);
                activityService.markBreakNotificationSent(userEmail);
            }
        }
    }
}
