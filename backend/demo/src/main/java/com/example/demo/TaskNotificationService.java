package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TaskNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendTaskReminder(String to, String taskName, String timeIST) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject("APMS PRO: Important Task Alert!");
            message.setText("Hello!\n\nThis is an automated reminder for your task: " + taskName +
                    "\nScheduled Time: " + timeIST + " (IST)" +
                    "\n\nYour task starts in 30 minutes. Please be ready!");

            mailSender.send(message);
            System.out.println("Reminder sent for task: " + taskName);
        } catch (Exception e) {
            System.err.println("Task Reminder Error: " + e.getMessage());
        }
    }
}
