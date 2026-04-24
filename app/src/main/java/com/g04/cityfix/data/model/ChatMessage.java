package com.g04.cityfix.data.model;

/**
 * Model class representing a chat message in the conversation
 * Used to display messages in the chat interface
 * @auther u7901628 Sonia Lin
 */
public class ChatMessage {
    public static final int TYPE_USER = 0; // Message from user
    public static final int TYPE_AI = 1;   // Message from AI

    private String content;  // The text content of the message
    private int type;        // Type of message (user or AI)
    private int userRole;    // Type of message (user or AI)

    /**
     * Constructor with default user role (citizen)
     * @param content The message content
     * @param type The message type (user or AI)
     */
    public ChatMessage(String content, int type) {
        this(content, type, 0);
    }

    /**
     * Constructor with specified user role
     * @param content The message content
     * @param type The message type (user or AI)
     * @param userRole The role of the user (0-citizen, 1-worker)
     */
    public ChatMessage(String content, int type, int userRole) {
        this.content = content;
        this.type = type;
        this.userRole = userRole;
    }

    /**
     * Get the message content
     * @return The text content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the message type
     * @return The type of message (TYPE_USER or TYPE_AI)
     */
    public int getType() {
        return type;
    }

    /**
     * Get the user role
     * @return The role of the user (0-citizen, 1-worker)
     */
    public int getUserRole() {
        return userRole;
    }
}
