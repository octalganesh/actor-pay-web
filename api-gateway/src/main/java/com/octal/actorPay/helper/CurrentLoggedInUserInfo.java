package com.octal.actorPay.helper;

import java.util.Objects;

/**
 * This class contains current logged in user info
 */
public class CurrentLoggedInUserInfo {

    private String username;
    private String userId;

    public CurrentLoggedInUserInfo() {
    }

    public CurrentLoggedInUserInfo(String userId, String username) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentLoggedInUserInfo that = (CurrentLoggedInUserInfo) o;
        return Objects.equals(username, that.username) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(username, userId);
    }

    @Override
    public String toString() {
        return "CurrentLoggedInUserInfo{" +
                "username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}