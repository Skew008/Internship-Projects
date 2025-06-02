package com.panicatthedebug.pathsync.service;

import com.panicatthedebug.pathsync.model.ActiveSession;
import com.panicatthedebug.pathsync.repository.ActiveSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ActivityTrackingService {

    @Autowired
    public ActiveSessionRepository sessionRepository;

    public ActiveSession recordActivity(String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        Optional<ActiveSession> existingSessionOpt = sessionRepository.findByUserEmailAndIsActiveTrue(userEmail);

        if (existingSessionOpt.isPresent()) {
            ActiveSession session = existingSessionOpt.get();
            long additionalSeconds = Duration.between(session.getLastActivityTime(), now).getSeconds();

            // Only count as activity if less than 5 minutes since last activity
            if (additionalSeconds < 300) {
                session.setTotalActiveTimeSeconds(session.getTotalActiveTimeSeconds() + additionalSeconds);
            }

            session.setLastActivityTime(now);
            return sessionRepository.save(session);
        } else {
            ActiveSession newSession = new ActiveSession();
            newSession.setUserEmail(userEmail);
            newSession.setStartTime(now);
            newSession.setLastActivityTime(now);
            newSession.setActive(true);
            newSession.setTotalActiveTimeSeconds(0);
            newSession.setBreakNotificationSent(false);
            return sessionRepository.save(newSession);
        }
    }

    public void endSession(String userEmail) {
        Optional<ActiveSession> existingSessionOpt = sessionRepository.findByUserEmailAndIsActiveTrue(userEmail);

        if (existingSessionOpt.isPresent()) {
            ActiveSession session = existingSessionOpt.get();
            session.setActive(false);
            sessionRepository.save(session);
        }
    }

    public int getCurrentActiveTimeMinutes(String userEmail) {
        Optional<ActiveSession> sessionOpt = sessionRepository.findByUserEmailAndIsActiveTrue(userEmail);
        return sessionOpt.map(session -> (int)(session.getTotalActiveTimeSeconds() / 60)).orElse(0);
    }

    public void markBreakNotificationSent(String userEmail) {
        Optional<ActiveSession> sessionOpt = sessionRepository.findByUserEmailAndIsActiveTrue(userEmail);
        sessionOpt.ifPresent(session -> {
            session.setBreakNotificationSent(true);
            sessionRepository.save(session);
        });
    }
}
