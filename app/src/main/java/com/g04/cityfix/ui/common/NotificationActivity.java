package com.g04.cityfix.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.R;
import com.g04.cityfix.data.model.Notification;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.model.User;
import com.g04.cityfix.domain.NotificationService;
import com.g04.cityfix.ui.citizen.ReportDetailCitizenActivity;
import com.g04.cityfix.ui.worker.ReportDetailWorkerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationActivity extends AppCompatActivity {
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private NotificationService notificationService = CityFixApplication.getNotificationService();;
    private User currentUser;

    /**
     * Initializes view elements
     * @param savedInstanceState
     * @author Junao Xiong
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationService = CityFixApplication.getNotificationService();
        currentUser = CityFixApplication.getUserService().getCurrentUser();

        // get data from firebase and update the local data
        notifications = new ArrayList<>();

        RecyclerView recyclerview = findViewById(R.id.reportsRecyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notifications, this::onNotificationClick);
        recyclerview.setAdapter(adapter);

        // pull out the data and update
        notificationService.loadNotificationsFromFirestore(
                currentUser.getUsername(),
                // if it works, put all notification into list and update notification
                new OnSuccessListener<List<Notification>>() {
                    @Override
                    public void onSuccess(List<Notification> list) {
                        notifications.clear();
                        notifications.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                },
                // if it doesn't work, send a exception
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(
                                NotificationActivity.this,
                                "Some mistakes happened when loading notification",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );

        // pull notification when log in
        CityFixApplication.getNotificationService().loadNotificationsFromFirestore(
                        currentUser.getUsername(),
                        list -> {},
                        e    -> {}
                );

        //  All readed
        Button markAll = findViewById(R.id.MarkAllAsRead);
        markAll.setOnClickListener(v -> {
            notificationService.markAllAsRead(currentUser.getUsername());
            for (Notification n : notifications) {
                n.setRead(true);
            }
            adapter.notifyDataSetChanged();
        });

        // quit button
        ImageButton quit = findViewById(R.id.quit_button);
        quit.setOnClickListener(v -> finish());
    }

    /**
     * Called when notification is clicked
     * @param n The notification be clicked
     * @author Junao Xiong
     */
    private void onNotificationClick(Notification n) {
        notificationService.markAsReadInFirestore(currentUser.getUsername(), n.getId());
        adapter.notifyDataSetChanged();
        n.setRead(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reports")
                .document(n.getReportId())
                .get()
                .addOnCompleteListener(task -> {
                    // if get reports correctly, get the report
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            RepairReport report = document.toObject(RepairReport.class);
                            Intent intent;
                            // if the role of user is worker, go to worker report detail page.
                            if (currentUser.getRole() == 1) {
                                intent = new Intent(this, ReportDetailWorkerActivity.class);
                            }
                            // if not, it must be citizen, then go to citizen report detail page.
                            else {
                                intent = new Intent(this, ReportDetailCitizenActivity.class);
                            }
                            intent.putExtra("report", report);
                            startActivity(intent);
                        }
                    }
                });
    }
}