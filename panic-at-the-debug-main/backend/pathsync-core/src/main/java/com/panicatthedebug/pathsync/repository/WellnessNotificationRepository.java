package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.WellnessNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WellnessNotificationRepository extends MongoRepository<WellnessNotification, String> {

    List<WellnessNotification> findByUserEmailAndAcknowledgedFalse(String userEmail);

    List<WellnessNotification> findByUserEmailAndSentAtAfterAndType(
            String userEmail,
            LocalDateTime after,
            WellnessNotification.NotificationType type
    );
}
