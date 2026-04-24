package com.g04.cityfix.ui.citizen;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.g04.cityfix.R;
import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.ui.common.FullScreenMapActivity;
import com.g04.cityfix.ui.common.FullscreenImageActivity;
import com.g04.cityfix.ui.common.ReportListActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * Activity of the page where citizen can view the details of a report
 * @author Jerry Yang
 */
public class ReportDetailCitizenActivity extends AppCompatActivity {
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
        // These two button is invisible for citizen
        findViewById(R.id.takeThisJob).setVisibility(View.GONE);
        findViewById(R.id.markAsDone).setVisibility(View.GONE);
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
    }
    private void setUpDetail(){
        title.setText(report.getTitle());
        citizenUsername.setText("From:"+report.getCitizenUsername());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
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
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
