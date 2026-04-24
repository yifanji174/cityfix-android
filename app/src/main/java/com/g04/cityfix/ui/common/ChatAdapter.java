package com.g04.cityfix.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.g04.cityfix.R;
import com.g04.cityfix.data.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying chat messages in a RecyclerView
 * Handles both user and AI message types with different layouts
 * @auther u7901628 Sonia Lin
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // List to store all chat messages
    private List<ChatMessage> messages = new ArrayList<>();

    /**
     * Creates appropriate ViewHolder based on message type
     * @param parent The parent ViewGroup
     * @param viewType The type of view (user or AI message)
     * @return ViewHolder for the specific message type
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_ai, parent, false);
            return new AIMessageViewHolder(view);
        }
    }

    /**
     * Binds data to the ViewHolder based on its type
     * @param holder The ViewHolder to bind data to
     * @param position The position of the item in the dataset
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AIMessageViewHolder) {
            ((AIMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * Returns the total number of items in the dataset
     * @return Size of the messages list
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Determines the view type based on the message type
     * @param position Position of the item in the dataset
     * @return Type of the message (user or AI)
     */
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    /**
     * Adds a new message to the chat and notifies the adapter
     * @param message The message to be added
     */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        try {
            notifyItemInserted(messages.size() - 1);
        } catch (NullPointerException e) {
            // Just for test
        }
    }

    /**
     * ViewHolder for user messages
     * Displays user message with appropriate avatar based on user role
     */
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private de.hdodenhof.circleimageview.CircleImageView avatarImage;

        /**
         * Constructor initializes view elements
         * @param itemView The view for this ViewHolder
         */
        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            avatarImage = itemView.findViewById(R.id.avatarImage);
        }

        /**
         * Binds message data to the view elements
         * @param message The message to display
         */
        public void bind(ChatMessage message) {
            messageText.setText(message.getContent());
            // Set user profile picture based on user role
            if (message.getUserRole() == 1) {
                avatarImage.setImageResource(R.drawable.worker);
            } else {
                avatarImage.setImageResource(R.drawable.citizen);
            }
        }
    }

    /**
     * ViewHolder for AI messages
     * Displays AI responses
     */
    static class AIMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        /**
         * Constructor initializes view elements
         * @param itemView The view for this ViewHolder
         */
        public AIMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }

        /**
         * Binds message data to the view elements
         * @param message The message to display
         */
        public void bind(ChatMessage message) {
            messageText.setText(message.getContent());
        }
    }
}
