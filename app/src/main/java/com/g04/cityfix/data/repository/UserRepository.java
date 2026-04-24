package com.g04.cityfix.data.repository;

import android.util.Log;

import com.g04.cityfix.common.utils.ObjectUtils;
import com.g04.cityfix.common.utils.ResponseResult;
import com.g04.cityfix.data.model.User;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

/**
 * Repository class for handling user data operations with Firestore
 * Provides methods for user CRUD operations
 */
public class UserRepository {
    private final FirebaseFirestore firestore;
    // Reference to the users collection in Firestore
    private final CollectionReference userCollection;

    /**
     * Constructor initializes Firestore and users collection reference
     * @param firestore The Firestore database instance
     */
    public UserRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.userCollection = firestore.collection("users");
    }

    /**
     * Adds a new user to the Firestore database
     * @param user The user object to be added
     * @param result Callback object to return operation result
     * @author Jerry Yang, u7901628 Sonia Lin
     */
    public void addUser(User user, ResponseResult result){
        //Cast User to a hashmap
        Map<String,Object> userData = ObjectUtils.toMap(user);
        //Add new user in firestore and show the result in log
        userCollection
                .document(user.getUsername())
                .set(userData)
                .addOnSuccessListener(e -> {
                    Log.d("AddUser", "Success");
                    result.setSuccess(true);
                    result.setE(null);
                })
                .addOnFailureListener(e ->{
                    Log.d("AddUser", "Failure");
                    result.setSuccess(false);
                    result.setE(e);
                });
    }

    /**
     * update the list of reports followed by user
     * @param username username
     * @param favoriteIds the id of reports
     * @author Yifan Ji
     */
    public void updateUserFavorites(String username, List<String> favoriteIds) {
        userCollection.document(username)
                .update("favorites", favoriteIds)
                .addOnSuccessListener(aVoid -> Log.d("UpdateFavorites", "Favorites updated successfully"))
                .addOnFailureListener(e -> Log.e("UpdateFavorites", "Failed to update favorites", e));
    }

    /**
     * Retrieves a user document by username
     * @param username The username to search for
     * @return Task containing the user document snapshot
     * @auther u7901628 Sonia Lin
     */
    public Task<DocumentSnapshot> getUserByUsername(String username) {
        return userCollection.document(username).get();
    }
}
