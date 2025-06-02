package com.panicatthedebug.pathsync.repository;

import com.panicatthedebug.pathsync.model.ResumeDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepo extends MongoRepository<ResumeDetails, String> {
    ResumeDetails findByEmail(String email);
}
