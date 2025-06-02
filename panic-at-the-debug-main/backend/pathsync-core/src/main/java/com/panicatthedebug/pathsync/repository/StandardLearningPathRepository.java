package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.StandardLearningPath;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface StandardLearningPathRepository extends MongoRepository<StandardLearningPath, String> {
    Optional<StandardLearningPath> findByTargetRoleAndTargetLanguage(String targetRole, String targetLanguage);
}
