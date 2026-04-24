package com.g04.cityfix.domain;

import com.g04.cityfix.CityFixApplication;
import com.g04.cityfix.common.constraints.ReportStatus;
import com.g04.cityfix.common.utils.ResponseResult;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.repository.ReportRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportService {
    private final ReportRepository reportRepository;

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // Add new report to Firestore
    public ResponseResult addReport(RepairReport report) {
        ResponseResult result = new ResponseResult();
        reportRepository.addReport(report, result);
        return result;
    }

    public void getReportsPaged(DocumentSnapshot lastDoc, int pageSize,
                                BiConsumer<List<RepairReport>, DocumentSnapshot> callback) {
        reportRepository.getReportsPaged(lastDoc, pageSize, callback);
    }

    public void getAllReports(Consumer<List<RepairReport>> callback) {
        reportRepository.getAllReports(callback);
    }


    /**
     * Mark a report as "Solved" and send notifications to favorited users
     * @param key key of a report
     * @author Jerry Yang, Junao Xiong
     */
    public void markAsDone(String key) {
        reportRepository.markAsDone(key);

        // Send notifications to user if the report is done
        FirebaseFirestore.getInstance().collection("reports").document(key).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        RepairReport report = documentSnapshot.toObject(RepairReport.class);

                        String title = "FINISHED! ";
                        String message = "The status of the report you favorited ‘" +
                                report.getTitle() + "’ was marked as done.";

                        List<String> users = report.getFavoriteUsernames();
                        if (users!= null){
                            for (String username : users) {
                                CityFixApplication.getNotificationService().sendNotificationToUser(username, title, message, key, report,3);
                            }
                        }
                    }
                });
    }
    /**
     * update images when mark a job as done
     * @param key key of a report
     * @param urls new list of urls
     * @author Jerry Yang
     */
    public void updateMarkAsDoneImages(String key, ArrayList<String> urls){
        reportRepository.updateMarkAsDoneImages(key,urls);
    }

    /**
     * mark a job as seen
     * @param key key of a report
     * @author Jerry Yang
     */
    public void markAsSeen(String key) {
        reportRepository.markAsSeen(key);
    }

    /**
     * mark a job as processing and send notification
     * @param key key of a report
     * @param favoriteByUser the username who take this job
     * @author Jerry Yang, Junao Xiong
     */
    public void takeThisJob(String key,String favoriteByUser) {
        reportRepository.markAsProcessing(key);

        // Send notifications to user if the report be take by workers
        reportRepository.favoriteByUser(key,favoriteByUser);
        FirebaseFirestore.getInstance().collection("reports").document(key).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        RepairReport report = documentSnapshot.toObject(RepairReport.class);

                        String title = "IN PROCESS~~~";
                        String message = "The status of the report you favorited ‘" +
                                report.getTitle() + "’ was taken by workers.";

                        List<String> users = report.getFavoriteUsernames();
                        if (users!=null){
                            for (String username : users) {
                                CityFixApplication.getNotificationService().sendNotificationToUser(username, title, message, key,report, 2);
                            }
                        }

                    }
                });
    }

    /**
     * check if the notification be checked 3 times by works, if so, send notifications.
     * @param report the report be checked 3 times
     * @author Junao Xiong
     */
    public void notifyOnThirdView(RepairReport report) {
        String key = report.getTimestamp().getTime() + "_" + report.getCitizenUsername();
        report.addViewedByStaffCount();
        firestore.collection("reports").document(key)
                .update("viewedByStaffCount", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    firestore.collection("reports").document(key).get()
                            .addOnSuccessListener(doc -> {
                                int count = doc.getLong("viewedByStaffCount").intValue();
                                int status = doc.getLong("status").intValue();
                                if (count == 3 && (status == ReportStatus.REPORT_SEEN || status == ReportStatus.REPORT_REPORTED)) {
                                    String title = "NEW CHECKED (·)";
                                    String message = "The report you marked ‘" + report.getTitle() + "’ was checked by workers multiple times";
                                    List<String> users = report.getFavoriteUsernames();
                                    if (users != null) {
                                        for (String user : users) {
                                            CityFixApplication.getNotificationService().sendNotificationToUser(user, title, message, key, report, 1);
                                        }
                                    }
                                }
                            });
                });
    }
}
