package com.g04.cityfix.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User{
    //Should be unique
    private String username;
    private String password;
    // "citizen" or "worker"
    private int role;
    private ArrayList<String> favorites;

    /**
     * Default constructor required for Firebase Firestore
     * Used for object deserialization when retrieving data from database
     * @auther u7901628 Sonia Lin
     */
    public User(){}

    public User(String username, String password, int role, ArrayList<String> favorites) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.favorites = favorites;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("role", role);
        return map;
    }
}
