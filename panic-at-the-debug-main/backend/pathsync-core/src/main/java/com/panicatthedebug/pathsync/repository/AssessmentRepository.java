package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.Assessment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends MongoRepository<Assessment, String> {
    Optional<Assessment> findByUserEmailAndCompletedFalse(String userEmail);
    List<Assessment> findByUserEmail(String userEmail);
}