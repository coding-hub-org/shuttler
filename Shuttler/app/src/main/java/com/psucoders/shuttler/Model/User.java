package com.psucoders.shuttler.Model;

import java.util.HashMap;

class Notifications {
    private String notifyLocation, timeAhead;
    private HashMap<String, Boolean> tokens;

    //    private boolean enabled;
    Notifications() {
    }

    public Notifications(HashMap<String, Boolean> tokens, String notifyLocation, String timeAhead) {
//        this.enabled = enabled;
        this.notifyLocation = notifyLocation;
        this.timeAhead = timeAhead;
        this.tokens = tokens;
    }

    public String getNotifyLocation() {
        return notifyLocation;
    }


    public void setNotificationTokens(HashMap<String, Boolean> tokens) {
        this.tokens = tokens;
    }

    public void setNotifyLocation(String notifyLocation) {
        this.notifyLocation = notifyLocation;
    }

    public String getTimeAhead() {
        return timeAhead;
    }

    public HashMap<String, Boolean> getTokens() {
        return tokens;
    }

    public void setTimeAhead(String timeAhead) {
        this.timeAhead = timeAhead;
    }

//    public boolean isEnabled() {
//        return enabled;
//    }

//    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }
}

public class User {
    private String username, email, password;

    private Notifications notifications;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public Notifications getNotifications() {
        return notifications;
    }

    public void setNotifications(HashMap<String, Boolean> b, String n, String t) {

        this.notifications = new Notifications(b, n, t);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

