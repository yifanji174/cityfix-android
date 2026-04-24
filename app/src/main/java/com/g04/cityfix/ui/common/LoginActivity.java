package com.g04.cityfix.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.data.repository.ReportRepository;
import com.g04.cityfix.domain.UserService;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for user login
 * Handles user authentication and navigation to main screen
 * @auther u7901628 Sonia Lin
 */
public class LoginActivity extends AppCompatActivity {
    // Service for user authentication and management
    private final UserService userService = CityFixApplication.getUserService();
    // UI components
    private EditText usernameInput;
    private EditText passwordInput;
    private CheckBox checkBox;
    private Button loginButton;
    // Repository for accessing report data
    private ReportRepository reportRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize report repository with Firestore instance
        reportRepository = new ReportRepository(FirebaseFirestore.getInstance());

        // Initialize UI components
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        checkBox = findViewById(R.id.checkBox);
        loginButton = findViewById(R.id.loginButton);

        // Set click listener for login button
        loginButton.setOnClickListener(v -> performLogin());
    }

    /**
     * Handles the login process
     * Validates input, authenticates user, and navigates to main screen
     */
    private void performLogin() {
        // Get user input
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Validate terms agreement
        if (!checkBox.isChecked()) {
            Toast.makeText(this, "Please agree to our terms!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter account and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt login with provided credentials
        userService.login(username, password)
                .addOnSuccessListener(isSuccess -> {
                    if (isSuccess) {
                        // Fetch all reports and load user favorites
                        reportRepository.getAllReports(allReports -> {
                            // Load favorite reports for the current user
                            userService.loadFavoritesForCurrentUser(allReports);
                            // Navigate to report list activity
                            Intent intent = new Intent(LoginActivity.this, ReportListActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        // Show error message for failed login
                        Toast.makeText(this, "Wrong account or password!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
