package com.g04.cityfix.domain;

import android.util.Log;

import com.g04.cityfix.data.model.Notification;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationService {

    private final Map<String, List<Notification>> notifications = new HashMap<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Send notifications to user
     * @param username the user who get the notificaitons
     * @param title the title of notification
     * @param message the description of notification
     * @param reportId the report ID of the report
     * @param report the report who sent notification
     * @param statues the statues of the notification
     * @author Junao Xiong
     */
    public void sendNotificationToUser(String username, String title, String message, String reportId, RepairReport report, int statues) {
        Notification noti = new Notification(title, message,reportId, report, statues);
        notifications.computeIfAbsent(username, k -> new ArrayList<>()).add(0, noti);
        db.collection("notifications")
                .document(username)
                .collection("messages")
                .document(noti.getId())
                .set(noti);
    }



    /**
     * Update the notification from firebase
     * @author Junao Xiong
     */
    public void loadNotificationsFromFirestore(
            String username,
            OnSuccessListener<List<Notification>> onSuccess,
            OnFailureListener onFailure
    ) {
        db.collection("notifications")
                .document(username)
                .collection("messages")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(qs -> {
                    List<Notification> list = new ArrayList<>();
                    for (DocumentSnapshot doc : qs.getDocuments()) {
                        Notification n = doc.toObject(Notification.class);
                        if (n!=null) list.add(n);
                    }
                    notifications.put(username, list);
                    onSuccess.onSuccess(list);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Mark one notifications as read
     * @param username the user who read notification
     * @param notificationId the notification be read
     * @author Junao Xiong
     */
    public void markAsReadInFirestore(String username, String notificationId) {
        // local
        notifications.getOrDefault(username, List.of())
                .stream().filter(n->n.getId().equals(notificationId))
                .forEach(n->n.setRead(true));
        // Firestore
        db.collection("notifications")
                .document(username)
                .collection("messages")
                .document(notificationId)
                .update("beRead", true);
    }


    /**
     * Mark all notifications as read
     * @param username the user who want to read all notification
     * @author Junao Xiong
     */
    public void markAllAsRead(String username) {
        List<Notification> list = notifications.get(username);
        if (list!=null) {
            list.forEach(n->n.setRead(true));
            db.collection("notifications")
                    .document(username)
                    .collection("messages")
                    .whereEqualTo("beRead", false)
                    .get()
                    .addOnSuccessListener(snaps -> {
                        for (DocumentSnapshot d: snaps) {
                            d.getReference().update("beRead", true);
                        }
                    });
        }
    }

    /**
     * get the number of unread notification
     * @param username the user
     * @return the number of unread notification
     * @author Junao Xiong
     */
    public int getUnreadCount(String username) {
        List<Notification> list = notifications.get(username);
        if (list == null) return 0;

        int count = 0;
        for (Notification noti : list) {
            if (!noti.getBeRead()) {
                count++;
            }
        }
        return count;
    }
}
