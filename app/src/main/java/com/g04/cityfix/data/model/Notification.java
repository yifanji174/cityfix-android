package com.g04.cityfix.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Notification {
    private String id;
    private String title;
    private String description;
    // this "time" include date and time
    private long time;

    private boolean beRead = false;

    private String reportId;

    private RepairReport report;

    private int statues; // 1=checked 3 times by works,2=in process,3=done

    public Notification() {}


    /**
     * Create a notification
     * @param title the title of notification
     * @param description the description of notification
     * @param reportId the report ID of the report
     * @param report the report who sent notification
     * @param statues the statues of the notification
     * @author Junao Xiong
     */
    public Notification(String title, String description, String reportId,RepairReport report, int statues) {
        this.time = System.currentTimeMillis();
        this.id = "notification_" + time;
        this.title = title;
        this.description = description;
        this.reportId = reportId;
        this.report = report;
        this.statues = statues;
    }

    //getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getTime() { return time; }
    public void setTime(Long time) { this.time = time; }
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    public RepairReport getNotificationReport() {
        return report;
    }
    public void setNotificationReport(RepairReport report) {
        this.report = report;
    }
    public long getAllDate() {
        return time;
    }
    // return if the notification is read or not
    public boolean getBeRead() {
        return beRead;
    }
    public void setRead(boolean read) {
        beRead = read;
    }
    public int getStatues() { return statues; }
    public void setStatues(int statues){
        this.statues = statues;
    }


    /**
     * Get the date of the notification be sent
     * @return the date of notification be sent, like Mar-01-2025
     * @author Junao Xiong
     */
    //  Return the date of notification
    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault());
        return sdf.format(time);
    }

    /**
     * Get the time of the notification be sent
     * @return the time of notification be sent, like 16:22
     * @author Junao Xiong
     */
    public String getTimeOfDayString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(time);
    }

}