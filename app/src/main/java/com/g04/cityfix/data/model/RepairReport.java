package com.g04.cityfix.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * @author Jerry Yang, Junhao Xiong, Yifan Ji
 */
public class RepairReport implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;

    private String title;
    private String description;
    private String citizenUsername;
    private int status;
    private Date timestamp;
    private ArrayList<String> imageURLs;
    private ArrayList<String> markAsDoneURLs;

    private String location;
    private String type;

    private boolean favorited;

    private ArrayList<String> favoriteUsernames;
    private int ViewedByStaffCount;

    public RepairReport() {
    }

    public RepairReport(String title, String description, String citizenUsername, int status,
                        Date timestamp, ArrayList<String> imageURLs) {
        this.title = title;
        this.description = description;
        this.citizenUsername = citizenUsername;
        this.status = status;
        this.timestamp = timestamp;
        this.imageURLs = imageURLs;
        this.id = generateId(timestamp, citizenUsername);
        this.ViewedByStaffCount = 0;
    }

    public RepairReport(String title, String description, String citizenUsername, int status,
                        Date timestamp, ArrayList<String> imageURLs, String location, String type) {
        this(title, description, citizenUsername, status, timestamp, imageURLs);
        this.location = location;
        this.type = type;
    }

    private String generateId(Date timestamp, String username) {
        return timestamp.getTime() + "_" + username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCitizenUsername() {
        return citizenUsername;
    }

    public void setCitizenUsername(String citizenUsername) {
        this.citizenUsername = citizenUsername;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
        this.id = generateId(timestamp, citizenUsername); // Re-generate ID
    }

    public ArrayList<String> getImageURLs() {
        return imageURLs;
    }

    public void setImageURLs(ArrayList<String> imageURLs) {
        this.imageURLs = imageURLs;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    // List of usernames who favorited this report
    public ArrayList<String> getFavoriteUsernames() {
        return favoriteUsernames;
    }

    public void setFavoriteUsernames(ArrayList<String> favoriteUsernames) {
        this.favoriteUsernames = favoriteUsernames;
    }

    public int getViewedByStaffCount() {
        return ViewedByStaffCount;
    }

    public void setViewedByStaffCount(int viewedByStaffCount) {
        this.ViewedByStaffCount = viewedByStaffCount;
    }

    public ArrayList<String> getMarkAsDoneURLs() {
        return markAsDoneURLs;
    }

    public void setMarkAsDoneURLs(ArrayList<String> markAsDoneURLs) {
        this.markAsDoneURLs = markAsDoneURLs;
    }

    public int addViewedByStaffCount() {
        ViewedByStaffCount++;
        return ViewedByStaffCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RepairReport other = (RepairReport) obj;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
