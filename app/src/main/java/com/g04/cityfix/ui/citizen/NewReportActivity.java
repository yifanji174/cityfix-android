package com.g04.cityfix.ui.citizen;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.constraints.ReportType;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.UserService;
import com.g04.cityfix.ui.common.MapSelectActivity;
import com.g04.cityfix.ui.common.NavigationBarActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Activity of the page where citizen can submit a new report
 * @author Jerry Yang
 */
public class NewReportActivity extends NavigationBarActivity {
    private final UserService userService = CityFixApplication.getUserService();
    private final ReportService reportService = CityFixApplication.getReportService();
    private static final int MAX_IMAGES = 3;
    private static final int REQUEST_CODE_SELECT_LOCATION = 2001;
    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private EditText titleInput;
    //Latitude of selected location
    private double lat;
    //Longitude of selected location
    private double lng;
    private boolean locationSelected = false;
    private EditText locationInput;
    private EditText descriptionInput;
    private Spinner spinnerType;
    private Button reportButton;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView imageView;
    private LinearLayout imageContainer;
    private ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private ArrayList<String> imageUrls = new ArrayList<>();
    private static final String IMGUR_CLIENT_ID = "c64e8b4e5f57932";
    private final ActivityResultLauncher<Intent> selectLocationLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            double lat = data.getDoubleExtra("lat", 0.0);
                            double lng = data.getDoubleExtra("lng", 0.0);
                            this.lat = lat;
                            this.lng = lng;
                            locationInput.setText("Done");
                            locationSelected = true;
                        }
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);
        setupNavigationBar();

        titleInput = findViewById(R.id.titleInput);
        locationInput = findViewById(R.id.locationInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        imageView = findViewById(R.id.newPicture);
        reportButton = findViewById(R.id.reportButton);
        imageContainer = findViewById(R.id.imageContainer);
        spinnerType = findViewById(R.id.spinnerType);
        //Set types for spinner
        String[] reportLabels = ReportType.getLabels();
        List<String> labelList = new ArrayList<>();
        labelList.add("Select...");  // index = 0
        Collections.addAll(labelList, reportLabels);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labelList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        findViewById(R.id.locationSelector).setOnClickListener(
                view -> {
                    Intent intent = new Intent(this, MapSelectActivity.class);
                    selectLocationLauncher.launch(intent);
                }
        );

        // Initialize Launcher for image pick
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        // To compute how many images can be selected
                        int remainingSlots = MAX_IMAGES - selectedImageUris.size();

                        if (data.getClipData() != null) {
                            // if multiple images are selected
                            int count = Math.min(data.getClipData().getItemCount(), remainingSlots);
                            for (int i = 0; i < count; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                //Limit for image size
                                if (isImageWithinLimit(imageUri)) {
                                    selectedImageUris.add(imageUri);
                                    addImageToLayout(imageUri);
                                    uploadImageToImgur(imageUri);
                                } else {
                                    Toast.makeText(this, "Image should be less than 5MB", Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (data.getClipData().getItemCount() > remainingSlots) {
                                Toast.makeText(this, "Only " + remainingSlots + " image(s) added. Max is 3.", Toast.LENGTH_SHORT).show();
                            }

                        } else if (data.getData() != null) {
                            // if only one is selected
                            if (selectedImageUris.size() < MAX_IMAGES) {
                                Uri imageUri = data.getData();
                                //Limit for image size
                                if (isImageWithinLimit(imageUri)) {
                                    selectedImageUris.add(imageUri);
                                    addImageToLayout(imageUri);
                                    uploadImageToImgur(imageUri);
                                } else {
                                    Toast.makeText(this,
                                            "Image should be less than 5MB", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Maximum 3 images allowed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Click to open image selector
        imageView.setOnClickListener(v -> {
            if (selectedImageUris.size() >= MAX_IMAGES) {
                Toast.makeText(this, "You can upload up to 3 images only.", Toast.LENGTH_SHORT).show();
                return;
            }
            openImagePicker();
        });

        // Register action for report button
        reportButton.setOnClickListener(
                View -> {
                    String title = titleInput.getText().toString();
                    String location = lat + "," + lng;
                    String description = descriptionInput.getText().toString();
                    int position = spinnerType.getSelectedItemPosition();
                    if (title.isBlank() || !locationSelected || position == 0) {
                        Toast.makeText(this, "Please complete the required information", Toast.LENGTH_LONG).show();
                    } else {
                        Date date = new Date();
                        String username = userService.getCurrentUser().getUsername();
                        RepairReport report = new RepairReport(title, description, username, ReportStatus.REPORT_REPORTED, date, imageUrls);
                        ReportType selectedType = ReportType.values()[position - 1];
                        report.setLocation(location);
                        report.setType(selectedType.getLabel());
                        //Add favorite by user
                        ArrayList<String> favoriteByUsers = new ArrayList<>();
                        favoriteByUsers.add(username);
                        report.setFavoriteUsernames(favoriteByUsers);
                        reportService.addReport(report);
                        Intent intent = new Intent(this, ReportDetailCitizenActivity.class);
                        intent.putExtra("report", report);
                        startActivity(intent);
                        userService.toggleFavorite(report);
                        Toast.makeText(this, "Report successful", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
        );

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Can pick mutiple images
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select up to 3 images"));
    }

    private void uploadImageToImgur(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytes(inputStream);
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            new Thread(() -> {
                try {
                    URL url = new URL("https://api.imgur.com/3/image");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
                    conn.setDoOutput(true);

                    String data = "image=" + URLEncoder.encode(encodedImage, "UTF-8");
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(data);
                    writer.flush();
                    writer.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());
                    String uploadedImageUrl = json.getJSONObject("data").getString("link");

                    runOnUiThread(() -> {
                        imageUrls.add(uploadedImageUrl);
                        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Upload failed", Toast.LENGTH_LONG).show());
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can't read file", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int len;
        byte[] data = new byte[1024];
        while ((len = inputStream.read(data)) != -1) {
            buffer.write(data, 0, len);
        }
        return buffer.toByteArray();
    }

    private void addImageToLayout(Uri imageUri) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
        params.setMargins(0, 0, 8, 0);
        imageView.setLayoutParams(params);
        imageView.setImageURI(imageUri);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageContainer.addView(imageView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    //To determine whether the image size is within limit
    private boolean isImageWithinLimit(Uri uri) {
        Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.SIZE}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long size = cursor.getLong(0);      // bytes
            cursor.close();
            return size <= MAX_IMAGE_BYTES;
        }
        // if the size is unknown, return false
        return false;
    }
}
