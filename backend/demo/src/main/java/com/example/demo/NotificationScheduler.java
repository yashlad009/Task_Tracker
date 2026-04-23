package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private ImportantTaskRepository repository;

    @Autowired
    private TaskNotificationService notificationService;

    // This constructor runs the moment Spring Boot starts
    public NotificationScheduler() {
        System.out.println("🚀 [SYSTEM] Notification Scheduler has been Initialized!");
    }

    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
    public void checkAndNotify() {
        // 1. Get current time in UTC
        LocalDateTime nowUtc = LocalDateTime.now(ZoneId.of("UTC"));

        // 2. Wide window to catch the task even with small lag
        LocalDateTime alertWindowStart = nowUtc.minusHours(1);
        LocalDateTime alertWindowEnd = nowUtc.plusMinutes(45);

        // Debug: Print to console so we know the clock is ticking
        System.out.println("⏰ [CLOCK] Checking DB at UTC: " + nowUtc.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // Fetch all unprocessed tasks
        List<ImportantTask> tasks = repository.findByProcessedFalse();

        if (tasks.isEmpty()) {
            System.out.println("ℹ️ No pending important tasks found in DB.");
        }

        for (ImportantTask task : tasks) {
            LocalDateTime taskTimeUtc = task.getEventTime();

            System.out.println("🔍 Checking Task: " + task.getTaskName() + " | Time: " + taskTimeUtc);

            // 3. The Logic Check
            if (taskTimeUtc.isAfter(alertWindowStart) && taskTimeUtc.isBefore(alertWindowEnd)) {

                try {
                    // Convert UTC to IST for the email body text
                    LocalDateTime istTime = taskTimeUtc.atZone(ZoneId.of("UTC"))
                            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                            .toLocalDateTime();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                    String formattedTimeIST = istTime.format(formatter);

                    System.out.println("🎯 MATCH! Triggering alert for: " + task.getTaskName());

                    notificationService.sendTaskReminder(
                            task.getUserEmail(),
                            task.getTaskName(),
                            formattedTimeIST
                    );

                    // 4. Mark as processed and save
                    task.setProcessed(true);
                    repository.save(task);
                    System.out.println("✅ SUCCESS: Task marked as processed in database.");

                } catch (Exception e) {
                    System.err.println("❌ MAIL ERROR: " + e.getMessage());
                }
            } else {
                System.out.println("⏩ Task '" + task.getTaskName() + "' is not in the 30-min window yet.");
            }
        }
    }
}