package com.g04.cityfix.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.g04.cityfix.R;
import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.data.model.Notification;
import com.g04.cityfix.domain.NotificationService;
import com.g04.cityfix.domain.UserService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.view.View;
import android.widget.ImageView;

/**
 * Activity for user profile management
 * Extends NavigationBarActivity to include navigation functionality
 * @auther u7901628 Sonia Lin
 */
public class ProfileActivity extends NavigationBarActivity {
    // Service for user-related operations
    private final UserService userService = CityFixApplication.getUserService();
    // Service for notification management
    private final NotificationService notificationService = CityFixApplication.getNotificationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set profile image based on user role
        de.hdodenhof.circleimageview.CircleImageView profileImage = findViewById(R.id.profileImage);
        if (userService.getCurrentUser().getRole() == 0) {
            profileImage.setImageResource(R.drawable.citizen);
        } else {
            profileImage.setImageResource(R.drawable.worker);
        }

        // Display username
        TextView usernameText = findViewById(R.id.usernameText);
        String username = userService.getCurrentUser().getUsername();
        usernameText.setText(username);

        // Initialize the notification button
        LinearLayout notificationButton = findViewById(R.id.notificationContainer);
        notificationButton.setOnClickListener(v -> gotoNotification());

        // Initialize the marked reports button (favorites)
        LinearLayout markedReportButton = findViewById(R.id.markedReportContainer);
        markedReportButton.setOnClickListener(v -> gotoMarkedReport());

        // Initialize the notification button
        LinearLayout qaButton = findViewById(R.id.qaContainer);
        qaButton.setOnClickListener(v -> gotoQA());

        // Initialize the about button
        LinearLayout aboutButton = findViewById(R.id.aboutContainer);
        aboutButton.setOnClickListener(v -> gotoAbout());

        // Initialize the logout button
        LinearLayout logoutButton = findViewById(R.id.logoutContainer);
        logoutButton.setOnClickListener(v -> performLogout());

        // Setup the bottom navigation bar
        setupNavigationBar();
    }

    /**
     * Navigate to Notification Page
     */
    private void gotoNotification() {
        // Temporary: go back to main activity (can be expanded later)
        Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to Favourite Reports Page
     */
    private void gotoMarkedReport() {
        // Open the page that shows the user's favorite reports
        Intent intent = new Intent(ProfileActivity.this, FavoriteReportListActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to Q&A Page
     */
    private void gotoQA() {
        Intent intent = new Intent(ProfileActivity.this, QAActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to About Page
     */
    private void gotoAbout() {
        Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Handle user logout
     * Clears user session and navigates to login screen
     */
    private void performLogout() {
        // Clear the logged-in user and return to the login page
        userService.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Update notification indicators when activity resumes
     * Shows or hides the notification red dot based on unread notifications
     */
    @Override
    protected void onResume() {
        super.onResume();
        String username = userService.getCurrentUser().getUsername();

        // set if the red dot of notification is visible or not
        notificationService.loadNotificationsFromFirestore(
                username,
                list -> {
                    // show red dot if the after update
                    int unreadCount = notificationService.getUnreadCount(username);
                    ImageView redDot = findViewById(R.id.Notification_redDot);
                    if (unreadCount > 0) {
                        redDot.setVisibility(View.VISIBLE);
                    } else {
                        redDot.setVisibility(View.GONE);
                    }
                },
                e -> {
                    // if something wrong happened, set red dot to GONE
                    ImageView redDot = findViewById(R.id.Notification_redDot);
                    redDot.setVisibility(View.GONE);
                }
        );
    }
}
