package com.g04.cityfix.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.g04.cityfix.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Activity that displays information about the application
 * Shows license information and notices from assets
 * @auther u7901628 Sonia Lin
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Initialize license and notice text view
        TextView licenseTextView = findViewById(R.id.licenseTextView);
        licenseTextView.setMovementMethod(LinkMovementMethod.getInstance());
        TextView noticeTextView = findViewById(R.id.noticeTextView);
        noticeTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // Set return button, navigate back to Profile Activity when return button is clicked
        ImageButton returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        // Read NOTICE from assets
        try {
            noticeTextView.setText(readFromAssets("NOTICE"));
        } catch (IOException e) {
            // Display error message if reading fails
            noticeTextView.setText("Failed to read NOTICE");
        }
    }

    /**
     * Reads content from a file in the assets folder
     *
     * @param filename The name of the file to read
     * @return The content of the file as a string
     * @throws IOException If there is an error reading the file
     */
    private String readFromAssets(String filename) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream is = getAssets().open(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            // Read file line by line and append to content
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}