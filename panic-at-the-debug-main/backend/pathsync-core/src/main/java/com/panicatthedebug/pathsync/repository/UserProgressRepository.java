package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.UserProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {
}
