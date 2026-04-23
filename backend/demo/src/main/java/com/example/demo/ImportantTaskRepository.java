package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ImportantTaskRepository extends MongoRepository<ImportantTask, String> {
    List<ImportantTask> findByUserId(String userId);
    List<ImportantTask> findByProcessedFalse(); // We only care about tasks not yet emailed
}
