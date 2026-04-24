package com.g04.cityfix.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.data.model.ChatMessage;
import com.g04.cityfix.domain.LLMService;
import com.g04.cityfix.domain.LLMServiceFactory;
import com.g04.cityfix.domain.UserService;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity for Q&A interaction with AI assistant
 * Allows users to ask questions and receive responses from the LLM service
 * @auther u7901628 Sonia Lin
 */
public class QAActivity extends AppCompatActivity {
    // Service for user-related operations
    private final UserService userService = CityFixApplication.getUserService();
    // UI components
    private TextInputEditText questionEditText;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ProgressBar progressBar;
    // Service for LLM interactions
    private LLMService llmService;
    // Current user's role
    private int userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa);

        // Get current user's role
        userRole = userService.getCurrentUser().getRole();

        // Initialize views
        questionEditText = findViewById(R.id.questionEditText);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        ImageButton sendButton = findViewById(R.id.sendButton);
        ImageButton returnButton = findViewById(R.id.returnButton);

        // Set up RecyclerView with adapter
        chatAdapter = new ChatAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Initialize LLM Service for AI interactions
        llmService = LLMServiceFactory.createHFInferenceProviderLLMService(this);

        // Configure send button to trigger question submission
        sendButton.setOnClickListener(v -> askQuestion());

        // Configure text input to handle send action from keyboard
        questionEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                askQuestion();
                return true;
            }
            return false;
        });

        // Configure return button to navigate back to profile page
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Display welcome message with privacy notice
        chatAdapter.addMessage(new ChatMessage("Hello! I'm CityFix AI. What can I do for you?\n" +
                "Your conversations with CityFix AI may be processed by third-party services. We prioritize your privacy, but please avoid sharing sensitive personal information.", ChatMessage.TYPE_AI));
    }

    /**
     * Send question to LLM
     * Processes user input, displays it in chat, and requests response from AI
     */
    private void askQuestion() {
        // Get and validate user input
        String question = questionEditText.getText().toString().trim();
        if (question.isEmpty()) {
            Toast.makeText(this, "Enter your question", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add user question to chat history
        chatAdapter.addMessage(new ChatMessage(question, ChatMessage.TYPE_USER, userRole));

        // Scroll to show the latest message
        chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Clear input field
        questionEditText.setText("");

        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Call LLM service to process the question
        llmService.askQuestion(question, new LLMService.LLMCallback() {
            @Override
            public void onSuccess(String answer) {
                runOnUiThread(() -> {
                    // Hide loading indicator
                    progressBar.setVisibility(View.GONE);
                    // Add AI response to chat history
                    chatAdapter.addMessage(new ChatMessage(answer, ChatMessage.TYPE_AI));
                    // Scroll to show the latest message
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    // Hide loading indicator
                    progressBar.setVisibility(View.GONE);
                    // Display error message in chat
                    chatAdapter.addMessage(new ChatMessage("Sorry, error：" + errorMessage, ChatMessage.TYPE_AI));
                    chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                    // Show error toast
                    Toast.makeText(QAActivity.this, "request failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });

    }
}
