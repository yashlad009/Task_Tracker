package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiChatService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1500L;

    public AiChatService(UserRepository userRepository, TaskRepository taskRepository, MilestoneRepository milestoneRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.milestoneRepository = milestoneRepository;
    }

    public String getChatResponse(String userId, String userMessage) {
        // 1. Gather User Context
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return "User not found. Cannot provide personalized advice.";
        }
        User user = userOpt.get();

        List<Task> tasks = taskRepository.findByUserId(userId);
        List<Milestone> milestones = milestoneRepository.findByUserId(userId);
        
        int level = Math.max(1, (user.getTokens() / 100) + 1);

        // 2. Build the Context Prompt
        StringBuilder context = new StringBuilder();
        context.append("You are an AI Mentor inside a productivity and task tracking application called 'APMS PRO' or 'TaskTracker'.\n");
        context.append("Your goal is to provide brief, encouraging, and highly strategic advice for career and task planning based on the user's current level and pending tasks.\n\n");
        
        context.append("### User Context:\n");
        context.append("- **Level:** ").append(level).append("\n");
        context.append("- **Tokens:** ").append(user.getTokens()).append("\n");
        context.append("- **Role:** ").append(user.getRole() != null ? user.getRole() : "Standard User").append("\n\n");

        context.append("### Active Tasks:\n");
        if (tasks.isEmpty()) {
            context.append("No active tasks at the moment.\n");
        } else {
            for (Task t : tasks) {
                context.append("- [").append(t.getStatus()).append("] ").append(t.getText()).append(" (Category: ").append(t.getCategory()).append(")\n");
            }
        }
        context.append("\n");

        context.append("### Active Milestones:\n");
        if (milestones.isEmpty()) {
            context.append("No active milestones at the moment.\n");
        } else {
            for (Milestone m : milestones) {
                context.append("- ").append(m.getName()).append(" (Progress: ").append(m.getProgress()).append("%)\n");
            }
        }
        context.append("\n");

        context.append("### User Query:\n");
        context.append(userMessage).append("\n\n");
        
        context.append("### Instructions:\n");
        context.append("Respond directly to the user query utilizing the context above. Keep the response formatted nicely using Markdown. Be motivational and practical.");

        // 3. Call Gemini API
        return callGeminiApi(context.toString());
    }

    private String callGeminiApi(String prompt) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return "AI Mentor is not configured. Set GEMINI_API_KEY in the environment and restart the backend.";
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = geminiApiUrl + "?key=" + geminiApiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request body based on Gemini API spec
        Map<String, Object> requestBody = new HashMap<>();
        
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> contentPart = new HashMap<>();
        List<Map<String, Object>> partsList = new ArrayList<>();
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        partsList.add(textPart);
        contentPart.put("parts", partsList);
        contents.add(contentPart);
        
        requestBody.put("contents", contents);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    Map<String, Object> body = response.getBody();
                    if (body.containsKey("candidates")) {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                        if (!candidates.isEmpty()) {
                            Map<String, Object> firstCandidate = candidates.get(0);
                            if (firstCandidate.containsKey("content")) {
                                Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                                if (content.containsKey("parts")) {
                                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                                    if (!parts.isEmpty() && parts.get(0).containsKey("text")) {
                                        return (String) parts.get(0).get("text");
                                    }
                                }
                            }
                        }
                    }
                }
                return "I'm sorry, I couldn't process your request right now. Please try again later.";
            } catch (org.springframework.web.client.ResourceAccessException e) {
                System.err.println("Connection timeout or network error with AI Mentor: " + e.getMessage());
                return "The AI Mentor is currently taking too long to respond. Please try again later.";
            } catch (org.springframework.web.client.HttpStatusCodeException e) {
                if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE && attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempt);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        return "The AI Mentor retry was interrupted. Please try again.";
                    }
                    continue;
                }

                System.err.println("HTTP Error from Gemini API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    return "AI Mentor is busy right now because the Gemini model is under high demand. Please try again in a moment.";
                }
                return "AI Mentor Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
            } catch (Exception e) {
                e.printStackTrace();
                return "An unexpected error occurred (" + e.getClass().getSimpleName() + "): " + e.getMessage();
            }
        }

        return "AI Mentor is busy right now because the Gemini model is under high demand. Please try again in a moment.";
    }
}
