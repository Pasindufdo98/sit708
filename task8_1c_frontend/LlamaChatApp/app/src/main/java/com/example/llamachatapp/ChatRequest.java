// ChatRequest.java
package com.example.llamachatapp;

public class ChatRequest {
    private String user;
    private String userMessage;

    public ChatRequest(String user, String userMessage) {
        this.user = user;
        this.userMessage = userMessage;
    }

    public String getUser() {
        return user;
    }

    public String getUserMessage() {
        return userMessage;
    }
}
