package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportantTaskRepository extends JpaRepository<ImportantTask, String> {
    List<ImportantTask> findByUserId(String userId);
    List<ImportantTask> findByProcessedFalse();
}
