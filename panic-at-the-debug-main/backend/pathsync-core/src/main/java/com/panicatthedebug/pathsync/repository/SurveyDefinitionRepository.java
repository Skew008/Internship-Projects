package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.SurveyDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SurveyDefinitionRepository extends MongoRepository<SurveyDefinition, String> {
    Optional<SurveyDefinition> findByTargetRoleAndLanguageAndActive(
            String targetRole, String language, boolean active);
}