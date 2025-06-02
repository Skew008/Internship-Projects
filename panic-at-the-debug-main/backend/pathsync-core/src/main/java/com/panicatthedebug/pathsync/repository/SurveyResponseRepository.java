package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.SurveyResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SurveyResponseRepository extends MongoRepository<SurveyResponse, String> {
    Optional<SurveyResponse> findByUserEmail(String userEmail);
}
