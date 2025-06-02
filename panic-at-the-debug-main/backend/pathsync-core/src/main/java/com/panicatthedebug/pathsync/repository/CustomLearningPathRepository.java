package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.CustomLearningPath;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomLearningPathRepository extends MongoRepository<CustomLearningPath, String> {
}
