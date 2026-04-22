package com.example.demo;

public class AiChatRequest {
    private String message;

    public AiChatRequest() {}

    public AiChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
