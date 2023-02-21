package com.greemlock.edutherapist.Objects;

import java.io.Serializable;

public class User implements Serializable {

    private String userUID;
    private String userDisplayName;
    private String userEmail;

    public User() {
    }

    public User(String userUID, String userDisplayName, String userEmail) {
        this.userUID = userUID;
        this.userDisplayName = userDisplayName;
        this.userEmail = userEmail;
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
}
