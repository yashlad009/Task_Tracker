package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/important")
@CrossOrigin(origins = "*")
public class ImportantTaskController {

    @Autowired
    private ImportantTaskRepository repository;

    @PostMapping("/add")
    public ImportantTask addImportantTask(@RequestBody ImportantTask task) {
        return repository.save(task);
    }

    @GetMapping("/user/{userId}")
    public List<ImportantTask> getTasks(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }
}