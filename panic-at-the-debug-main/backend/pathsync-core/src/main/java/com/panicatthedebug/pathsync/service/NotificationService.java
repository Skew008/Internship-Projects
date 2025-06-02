package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.model.WellnessNotification;
import com.panicatthedebug.pathsync.repository.WellnessNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private WellnessNotificationRepository notificationRepository;

    public WellnessNotification createBreakReminder(String userEmail, int activeMinutes) {
        WellnessNotification notification = new WellnessNotification();
        notification.setUserEmail(userEmail);
        notification.setMessage("You've been active for " + activeMinutes + " minutes. Consider taking a short break for your well-being.");
        notification.setType(WellnessNotification.NotificationType.BREAK_REMINDER);
        notification.setSentAt(LocalDateTime.now());
        notification.setAcknowledged(false);

        return notificationRepository.save(notification);
    }

    public boolean hasRecentBreakNotification(String userEmail, int minutesThreshold) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(minutesThreshold);
        List<WellnessNotification> recentNotifications = notificationRepository.findByUserEmailAndSentAtAfterAndType(
                userEmail, threshold, WellnessNotification.NotificationType.BREAK_REMINDER
        );

        return !recentNotifications.isEmpty();
    }

    public List<WellnessNotification> getPendingNotifications(String userEmail) {
        return notificationRepository.findByUserEmailAndAcknowledgedFalse(userEmail);
    }

    public void acknowledgeNotification(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setAcknowledged(true);
            notificationRepository.save(notification);
        });
    }
}
