package com.greemlock.edutherapist.Objects;

import android.app.Notification;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String userUID;
    private String userDisplayName;
    private String userEmail;
    private ArrayList<String> userFriends;

    public User() {
    }

    public User(String userUID, String userDisplayName, String userEmail, ArrayList<String> userFriends) {
        this.userUID = userUID;
        this.userDisplayName = userDisplayName;
        this.userEmail = userEmail;
        this.userFriends = userFriends;
    }

    public String getUserUID() {
        return userUID;
    }
    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }
    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ArrayList<String> getUserFriends() {
        return userFriends;
    }
    public void setUserFriends(ArrayList<String> userFriends) {
        this.userFriends = userFriends;
    }
}
