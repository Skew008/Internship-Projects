package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.ActiveSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveSessionRepository extends MongoRepository<ActiveSession, String> {
    Optional<ActiveSession> findByUserEmailAndIsActiveTrue(String userEmail);
}
