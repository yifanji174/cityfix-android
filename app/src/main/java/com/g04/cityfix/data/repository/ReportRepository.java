package com.g04.cityfix.data.repository;

import android.util.Log;

import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.utils.ObjectUtils;
import com.g04.cityfix.common.utils.ResponseResult;
import com.g04.cityfix.data.model.RepairReport;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.Map;
import java.util.function.BiConsumer;

public class ReportRepository {

    private final FirebaseFirestore firestore;
    private final CollectionReference reportCollection;

    public ReportRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.reportCollection = firestore.collection("reports");
    }

    /**
     * Add a new RepairReport to Firestore.
     * @param report a new report
     * @param result callback result
     * @author Jerry Yang
     */
    public void addReport(RepairReport report, ResponseResult result) {
        Map<String, Object> reportData = ObjectUtils.toMap(report);

        reportCollection
                .document(report.getTimestamp().getTime() + "_" + report.getCitizenUsername())
                .set(reportData)
                .addOnSuccessListener(e -> {
                    Log.d("AddReport", "Success");
                    result.setSuccess(true);
                    result.setE(null);
                })
                .addOnFailureListener(e -> {
                    Log.d("AddReport", "Failure");
                    result.setSuccess(false);
                    result.setE(e);
                });
    }


    /**
     * Load a page of reports ordered by timestamp descending.
     * @param lastDoc The last document
     * @param pageSize size of page
     * @author Yifan Ji, Junhao Liu
     */
    public void getReportsPaged(DocumentSnapshot lastDoc, int pageSize,
                                BiConsumer<List<RepairReport>, DocumentSnapshot> callback) {
        Query query = reportCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(pageSize);
        if (lastDoc != null) {
            query = query.startAfter(lastDoc);
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RepairReport> reports = new ArrayList<>();
                    List<DocumentSnapshot> docs = querySnapshot.getDocuments();
                    for (DocumentSnapshot doc : docs) {
                        reports.add(doc.toObject(RepairReport.class));
                    }
                    DocumentSnapshot lastVisible = docs.isEmpty() ? null : docs.get(docs.size() - 1);
                    callback.accept(reports, lastVisible);
                })
                .addOnFailureListener(e -> {
                    Log.e("Paging", "Failed to load paged reports", e);
                    callback.accept(new ArrayList<>(), null);
                });
    }

//Load all repair reports from Firestore.
    public void getAllReports(Consumer<List<RepairReport>> callback) {
        reportCollection.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<RepairReport> reports = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        reports.add(doc.toObject(RepairReport.class));
                    }
                    callback.accept(reports);
                })
                .addOnFailureListener(e -> {
                    Log.e("GetReports", "Failed to fetch reports", e);
                    callback.accept(new ArrayList<>());
                });
    }

    /**
     * Mark a report as done
     * @param key the key of a report
     * @author Jerry Yang
     */
    public void markAsDone(String key) {
        reportCollection.document(key).update("status", ReportStatus.REPORT_SOLVED);
    }
    /**
     * Mark a report as seen
     * @param key the key of a report
     * @author Jerry Yang
     */
    public void markAsSeen(String key) {
        reportCollection.document(key).update("status", ReportStatus.REPORT_SEEN);
    }
    /**
     * Mark a report as processing
     * @param key the key of a report
     * @author Jerry Yang
     */
    public void markAsProcessing(String key) {
        reportCollection.document(key).update("status", ReportStatus.REPORT_PROCESSING);
    }
    /**
     * update images when marked as done
     * @param key the key of a report
     * @author Jerry Yang
     */
    public void updateMarkAsDoneImages(String key,ArrayList<String> urls) {
        reportCollection.document(key).update("markAsDoneURLs", urls);
    }
    /**
     * Add user in the list of favorite users in a report
     * @param key the key of a report
     * @param favoriteByUser username
     * @author Jerry Yang
     */
    public void favoriteByUser(String key,String favoriteByUser){
        reportCollection.document(key).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        RepairReport report = documentSnapshot.toObject(RepairReport.class);
                        ArrayList<String> favoriteByUsers = report.getFavoriteUsernames();
                        if (favoriteByUsers == null){
                            favoriteByUsers = new ArrayList<>();
                        }
                        favoriteByUsers.add(favoriteByUser);
                        reportCollection.document(key).update("favoriteUsernames",favoriteByUsers);
                    }
                });
    }
}
