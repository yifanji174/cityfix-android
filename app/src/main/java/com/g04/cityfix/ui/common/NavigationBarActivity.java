package com.g04.cityfix.ui.common;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.UserRole;
import com.g04.cityfix.domain.UserService;
import com.g04.cityfix.ui.citizen.NewReportActivity;

/**
 * Navigation bar
 * @author Jerry Yang, Yifan Ji
 */

public class NavigationBarActivity extends AppCompatActivity {
    private final UserService userService = CityFixApplication.getUserService();

    protected void setupNavigationBar() {
        if (userService.getCurrentUser() == null) {
            // No user logged in, redirect to login page
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();  // Finish current Activity to avoid crash
            return;
        }

        int userType = userService.getCurrentUser().getRole();

        View newReportButton = findViewById(R.id.nav_new_report);

        // Set visibility for different user type
        if (userType == UserRole.USER_CITIZEN) {
            newReportButton.setVisibility(View.VISIBLE);
        } else if (userType == UserRole.USER_WORKER) {
            newReportButton.setVisibility(View.GONE);
        }

        // Set action listener for buttons
        findViewById(R.id.nav_home).setOnClickListener(v -> openHome());
        findViewById(R.id.nav_new_report).setOnClickListener(v -> openNewReport());
        findViewById(R.id.nav_profile).setOnClickListener(v -> openProfile());
    }

    private void openHome(){
        if (!(this.getClass().getSimpleName().equals("ReportListActivity"))) {
            Intent intent = new Intent(this, ReportListActivity.class);
            startActivity(intent);
        }
    }
    private void openNewReport(){
        if (!(this.getClass().getSimpleName().equals("NewReportActivity"))) {
            Intent intent = new Intent(this, NewReportActivity.class);
            startActivity(intent);
        }
    }
    private void openProfile(){
        if (!(this.getClass().getSimpleName().equals("ProfileActivity"))) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
