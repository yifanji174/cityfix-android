package com.g04.cityfix.domain;

import android.util.Log;

import com.g04.cityfix.common.utils.ResponseResult;
import com.g04.cityfix.data.model.RepairReport;
import com.g04.cityfix.data.model.User;
import com.g04.cityfix.data.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for user-related operations
 * Manages user authentication, favorites, and user data
 */
public class UserService {
    private final UserRepository userRepository;
    private User currentUser;
    private List<RepairReport> favoriteReportsLocal = new ArrayList<>();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Adds a new user to the system after checking if username exists
     * @param user The user object to be added
     * @auther u7901628 Sonia Lin
     */
    public void addUser(User user) {
        ResponseResult result = new ResponseResult();
        Task<DocumentSnapshot> checkResult = userRepository.getUserByUsername(user.getUsername());
        checkResult.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    userRepository.addUser(user, result);
                }
            } else {
                Log.e("Firestore", "Query failed", task.getException());
            }
        });
    }

    /**
     * Authenticates a user with username and password
     * @param username The username for login
     * @param password The password for login
     * @return Task with Boolean result indicating success or failure
     * @auther u7901628 Sonia Lin
     */
    public Task<Boolean> login(String username, String password) {
        return userRepository.getUserByUsername(username)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        if (user != null && user.getPassword().equals(password)) {
                            currentUser = user;
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * Sync user after login
     * @author Yifan Ji
     */
    public void reloadUserFromFirestore(Runnable callback) {
        if (currentUser == null) return;

        userRepository.getUserByUsername(currentUser.getUsername())
                .addOnSuccessListener(documentSnapshot -> {
                    User updatedUser = documentSnapshot.toObject(User.class);
                    if (updatedUser != null) {
                        currentUser = updatedUser;
                    }
                    if (callback != null) callback.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("UserService", "Failed to reload user from Firestore", e);
                    if (callback != null) callback.run();
                });
    }

    /**
     * Get current login user
     * @return currentUser the user currently login
     * @auther u7901628 Sonia Lin
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logs out the current user and clears local data
     * @auther u7901628 Sonia Lin
     */
    public void logout() {
        currentUser = null;
        favoriteReportsLocal.clear();
    }

    public List<RepairReport> getFavoriteReportsLocal() {
        return favoriteReportsLocal;
    }

    public void setFavoriteReportsLocal(List<RepairReport> favoriteReportsLocal) {
        this.favoriteReportsLocal = favoriteReportsLocal;
    }

    /**
     * Follow or unfollow a report
     * @param report the target report
     * @return sucess or not
     * @author Yifan Ji
     */
    public boolean toggleFavorite(RepairReport report) {
        boolean removed = favoriteReportsLocal.remove(report);
        if (!removed) {
            favoriteReportsLocal.add(report);
        }
        syncFavorites(favoriteReportsLocal);
        return !removed;
    }
    /**
     * sync favorite list of a user to firestore
     * @param reports the list to be synced
     * @author Yifan Ji
     */
    public void syncFavorites(List<RepairReport> reports) {
        this.favoriteReportsLocal = new ArrayList<>(reports);
        if (currentUser != null) {
            ArrayList<String> ids = convertToReportIds(reports);
            currentUser.setFavorites(ids);
            userRepository.updateUserFavorites(currentUser.getUsername(), ids);
        }
    }

    /**
     * Converte a list of reports to a list of their ids
     * @param reports reports to be converted
     * @return a list of strings
     * @author Yifan Ji
     */
    private ArrayList<String> convertToReportIds(List<RepairReport> reports) {
        ArrayList<String> ids = new ArrayList<>();
        for (RepairReport report : reports) {
            ids.add(report.getTimestamp().getTime() + "_" + report.getCitizenUsername());
        }
        return ids;
    }
    /**
     * load reports from current user
     * @author Yifan Ji
     */
    public void loadFavoritesForCurrentUser(List<RepairReport> allReports) {
        if (currentUser == null || currentUser.getFavorites() == null) return;

        List<String> favIds = currentUser.getFavorites();
        List<RepairReport> matched = new ArrayList<>();

        for (RepairReport report : allReports) {
            String id = report.getTimestamp().getTime() + "_" + report.getCitizenUsername();
            if (favIds.contains(id)) {
                matched.add(report);
            }
        }

        favoriteReportsLocal = matched;
    }
}
