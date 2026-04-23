package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/milestones")
@CrossOrigin(origins = "*")
public class MilestoneController {

    @Autowired
    private MilestoneRepository milestoneRepository;

    /**
     * ADD / UPDATE: Saves a milestone.
     * If the JSON includes an "id", it updates the existing record.
     */
    @PostMapping("/add")
    public Milestone addMilestone(@RequestBody Milestone milestone) {
        return milestoneRepository.save(milestone);
    }

    /**
     * FETCH PERSONAL: Retrieves milestones for a specific user.
     * Used by the standard User Dashboard.
     */
    @GetMapping("/user/{userId}")
    public List<Milestone> getByUserId(@PathVariable String userId) {
        return milestoneRepository.findByUserId(userId);
    }

    /**
     * FETCH ALL (ADMIN ONLY): Retrieves every milestone in the database.
     * This is the critical method for your Admin Portal's global monitor.
     */
    @GetMapping("/all")
    public List<Milestone> getAllMilestones() {
        return milestoneRepository.findAll();
    }

    /**
     * DELETE: Removes a milestone by its unique String ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMilestone(@PathVariable String id) {
        try {
            milestoneRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * UPDATE PROGRESS: Updates the percentage of a specific milestone.
     */
    @PutMapping("/{id}/update")
    public Milestone updateProgress(@PathVariable String id, @RequestParam int progress) {
        Milestone milestone = milestoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milestone not found"));
        milestone.setProgress(progress);
        return milestoneRepository.save(milestone);
    }
}