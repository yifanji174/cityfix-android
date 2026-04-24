package com.g04.cityfix.ui.worker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.constraints.UserRole;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.model.User;
import com.g04.cityfix.domain.ReportService;
import com.g04.cityfix.domain.UserService;
import com.g04.cityfix.ui.common.FullScreenMapActivity;
import com.g04.cityfix.ui.common.FullscreenImageActivity;
import com.g04.cityfix.ui.common.NavigationBarActivity;
import com.g04.cityfix.ui.common.ReportListActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Activity of the page where workers can view the details of a report and manage its status
 * @author Jerry Yang
 */
public class ReportDetailWorkerActivity extends NavigationBarActivity {
    private final UserService userService = CityFixApplication.getUserService();
    private final ReportService reportService = CityFixApplication.getReportService();
    private TextView title;
    private TextView citizenUsername;
    private TextView reportDate;
    private ImageView imageViewStatus;
    private TextView description;
    private ImageView buttonToMain;
    private Button mapView;
    private TextView typeTextView;
    private LinearLayout imageContainer;
    private LinearLayout imageContainerMarkAsDone;
    private RepairReport report;

    private static final int MAX_IMAGES = 3;
    private static final String IMGUR_CLIENT_ID = "c64e8b4e5f57932";
    private final ArrayList<Uri> selectedImageUris = new ArrayList<>();
    private final ArrayList<String> uploadedImageUrls = new ArrayList<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        report = (RepairReport) getIntent().getSerializableExtra("report", RepairReport.class);
        title = findViewById(R.id.titleTextView);
        citizenUsername = findViewById(R.id.citizenTextView);
        reportDate = findViewById(R.id.reportDateTextView);
        imageViewStatus = findViewById(R.id.imageViewStatus);
        description = findViewById(R.id.descriptionTextView);
        mapView = findViewById(R.id.mapView);
        typeTextView = findViewById(R.id.typeTextView);
        buttonToMain = findViewById(R.id.buttonToMain);
        imageContainer = findViewById(R.id.imagesLinerView);
        imageContainerMarkAsDone = findViewById(R.id.imagesLinerViewMarkAsDone);
        findViewById(R.id.takeThisJob).setVisibility(View.VISIBLE);
        findViewById(R.id.markAsDone).setVisibility(View.VISIBLE);
        findViewById(R.id.takeThisJob).setOnClickListener(e->{
            if (report.getStatus()<=ReportStatus.REPORT_SEEN){
                report.setStatus(ReportStatus.REPORT_PROCESSING);
                imageViewStatus.setImageResource(R.drawable.statues_in_process);
                reportService.takeThisJob(report.getTimestamp().getTime()+"_"+report.getCitizenUsername(),userService.getCurrentUser().getUsername());
                userService.toggleFavorite(report);
                Toast.makeText(this, "Taken Successfully", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "You can't take this job", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.markAsDone).setOnClickListener(e->{
            if (report.getStatus() <= ReportStatus.REPORT_SEEN){
                Toast.makeText(this, "You have to take this job first.", Toast.LENGTH_SHORT).show();
            } else if (report.getStatus() == ReportStatus.REPORT_SOLVED) {
                Toast.makeText(this, "This issue is already solved.", Toast.LENGTH_SHORT).show();
            }
            else {
                performeSolve(report);
            }
        });
        setUpDetail();
        buttonToMain.setOnClickListener(e->{
            Intent intent = new Intent(this, ReportListActivity.class);
            startActivity(intent);
            finish();
        });
        mapView.setOnClickListener(e->{
            Intent intent = new Intent(this, FullScreenMapActivity.class);
            intent.putExtra("report", report);
            startActivity(intent);
        });
        // Register for image pick when mark as done
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        int remain = MAX_IMAGES - selectedImageUris.size();
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            int count = Math.min(data.getClipData().getItemCount(), remain);
                            for (int i = 0; i < count; i++) {
                                Uri uri = data.getClipData().getItemAt(i).getUri();
                                if (isImageWithinLimit(uri)){
                                    selectedImageUris.add(uri);
                                    uploadImageToImgur(uri, this::tryFinishSolve);
                                }
                                else {
                                    Toast.makeText(this, "Image should be less than 5MB", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (data.getData() != null) {
                            Uri uri = data.getData();
                            if (isImageWithinLimit(uri)){
                                selectedImageUris.add(uri);
                                uploadImageToImgur(uri, this::tryFinishSolve);
                            }else {
                                Toast.makeText(this, "Image should be less than 5MB", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
    private void setUpDetail(){
        title.setText(report.getTitle());
        citizenUsername.setText("From:"+report.getCitizenUsername());
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss dd-mm-yyyy");
        String formattedDate = formatter.format(report.getTimestamp());
        reportDate.setText(formattedDate);
        typeTextView.setText(report.getType());
        switch (report.getStatus()){
            case ReportStatus.REPORT_REPORTED:
                imageViewStatus.setImageResource(R.drawable.statues_not_start_yet);
                break;
            case ReportStatus.REPORT_SEEN:
                imageViewStatus.setImageResource(R.drawable.statues_be_seen);
                break;
            case ReportStatus.REPORT_PROCESSING:
                imageViewStatus.setImageResource(R.drawable.statues_in_process);
                break;
            case ReportStatus.REPORT_SOLVED:
                imageViewStatus.setImageResource(R.drawable.statues_finished);
                break;
            default:
                imageViewStatus.setImageResource(R.drawable.statues_not_start_yet);
                break;
        }
        if (report.getDescription().isEmpty()){
            description.setText("No description.");
        }
        else {
            description.setText(report.getDescription());
        }
        description.setText(report.getDescription());
        ArrayList<String> urls = report.getImageURLs();
        //Set up images of broken items
        if (urls == null||urls.isEmpty()){
            findViewById(R.id.pictureTitle).setVisibility(View.GONE);
            imageContainer.setVisibility(View.GONE);
        }
        else {
            for (String url : urls) {
                ImageView imageView = new ImageView(this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
                params.setMargins(0, 0, 10, 0);
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(this)
                        .load(url)
                        .into(imageView);

                // Click to see the full image
                imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, FullscreenImageActivity.class);
                    intent.putExtra("image_url", url);
                    startActivity(intent);
                });

                imageContainer.addView(imageView);
            }
            //Set up images for job done
            urls = report.getMarkAsDoneURLs();
            if (urls == null||urls.isEmpty()){
                imageContainerMarkAsDone.setVisibility(View.GONE);
            }
            else {
                for (String url : urls) {
                    ImageView imageView = new ImageView(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(80), dpToPx(80));
                    params.setMargins(0, 0, 10, 0);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    Glide.with(this)
                            .load(url)
                            .into(imageView);

                    // Click to see the full image
                    imageView.setOnClickListener(v -> {
                        Intent intent = new Intent(this, FullscreenImageActivity.class);
                        intent.putExtra("image_url", url);
                        startActivity(intent);
                    });

                    imageContainerMarkAsDone.addView(imageView);
                }
            }
        }
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void performeSolve(RepairReport report){
        User user = userService.getCurrentUser();
        if (user.getRole()!= UserRole.USER_WORKER){
            Toast.makeText(this, "Only workers can solve.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!user.getFavorites().contains(report.getTimestamp().getTime()+"_"+report.getCitizenUsername())){
            Toast.makeText(this, "You didn't take this job.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUris.isEmpty()) {
            openImagePicker();
        } else {
            tryFinishSolve();
        }
    }
    private void tryFinishSolve() {
        if (uploadedImageUrls.size() < selectedImageUris.size()) return;

        report.setStatus(ReportStatus.REPORT_SOLVED);
        report.setMarkAsDoneURLs(uploadedImageUrls);
        reportService.markAsDone(report.getTimestamp().getTime()+"_"+report.getCitizenUsername());
        reportService.updateMarkAsDoneImages(report.getId(),uploadedImageUrls);
        imageViewStatus.setImageResource(R.drawable.statues_finished);
        Toast.makeText(this, "Issue solved!", Toast.LENGTH_SHORT).show();
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select up to 3 images"));
    }

    private boolean isImageWithinLimit(Uri uri) {
        Cursor cursor = getContentResolver().query(uri,
                new String[]{OpenableColumns.SIZE}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long size = cursor.getLong(0);      // bytes
            cursor.close();
            return size <= MAX_IMAGE_BYTES;
        }
        return false;
    }

    private void uploadImageToImgur(Uri uri, Runnable onSuccessAll) {
        new Thread(() -> {
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                byte[] bytes = getBytes(in);
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.imgur.com/3/image").openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Authorization", "Client-ID " + IMGUR_CLIENT_ID);
                new OutputStreamWriter(conn.getOutputStream())
                        .append("image=").append(java.net.URLEncoder.encode(encoded, "UTF-8"))
                        .flush();
                
                String link = new JSONObject(new java.io.BufferedReader(
                        new java.io.InputStreamReader(conn.getInputStream()))
                        .readLine())
                        .getJSONObject("data")
                        .getString("link");

                runOnUiThread(() -> {
                    uploadedImageUrls.add(link);
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                    onSuccessAll.run();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private byte[] getBytes(InputStream is) throws java.io.IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, nRead);
        return buffer.toByteArray();
    }
}
