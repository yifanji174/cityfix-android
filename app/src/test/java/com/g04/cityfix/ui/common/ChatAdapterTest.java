package com.g04.cityfix.ui.common;

import com.g04.cityfix.data.model.ChatMessage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ChatAdapter Class
 * @author u7901628 Sonia Lin
 */
public class ChatAdapterTest {

    private ChatAdapter chatAdapter;

    @Before
    public void setUp() {
        chatAdapter = new ChatAdapter();
    }

    @Test
    public void getItemCount_emptyAdapter_returnsZero() {
        // Test that an empty adapter returns 0 items
        assertEquals(0, chatAdapter.getItemCount());
    }

    @Test
    public void addMessage_increasesItemCount() {
        // Test that adding a message increases the item count
        ChatMessage message = new ChatMessage("Test message", ChatMessage.TYPE_USER, 0);
        chatAdapter.addMessage(message);
        assertEquals(1, chatAdapter.getItemCount());
    }

    @Test
    public void getItemViewType_userMessage_returnsUserType() {
        // Test that user messages return the correct view type
        ChatMessage message = new ChatMessage("User message", ChatMessage.TYPE_USER, 0);
        chatAdapter.addMessage(message);
        assertEquals(ChatMessage.TYPE_USER, chatAdapter.getItemViewType(0));
    }

    @Test
    public void getItemViewType_aiMessage_returnsAIType() {
        // Test that AI messages return the correct view type
        ChatMessage message = new ChatMessage("AI message", ChatMessage.TYPE_AI, 0);
        chatAdapter.addMessage(message);
        assertEquals(ChatMessage.TYPE_AI, chatAdapter.getItemViewType(0));
    }

    @Test
    public void addMultipleMessages_correctOrder() {
        // Test that messages are stored in the correct order
        ChatMessage message1 = new ChatMessage("First message", ChatMessage.TYPE_USER, 0);
        ChatMessage message2 = new ChatMessage("Second message", ChatMessage.TYPE_AI, 0);
        ChatMessage message3 = new ChatMessage("Third message", ChatMessage.TYPE_USER, 1);

        chatAdapter.addMessage(message1);
        chatAdapter.addMessage(message2);
        chatAdapter.addMessage(message3);

        assertEquals(3, chatAdapter.getItemCount());
        assertEquals(ChatMessage.TYPE_USER, chatAdapter.getItemViewType(0));
        assertEquals(ChatMessage.TYPE_AI, chatAdapter.getItemViewType(1));
        assertEquals(ChatMessage.TYPE_USER, chatAdapter.getItemViewType(2));
    }
}