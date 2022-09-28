package com.octal.actorPay.model;

import com.octal.actorPay.dto.FcmUserNotificationDTO;

public class FcmNotificationBean {

    private String to;
    private Data data;
    private Notification notification;
    private String priority = "high";
    private boolean mutable_content = false;
    private String category = null;
    private boolean content_available = true;

    public static class Data {
        private String title;
        private String message;
        private boolean background;
        private String imageUrl;
        private String timeStamp;
        private String type;
        private String typeId;
        private boolean adminNotification = false;

        // For sending Message Only
        public Data(String title, String message, String type, String typeId) {
            this.title = title;
            this.message = message;
            this.background = false;
            this.timeStamp = System.currentTimeMillis() + "";
            this.type = type;
            this.typeId = typeId;
        }

        // For sending Message with Image
        public Data(String title, String message, String type, String imageUrl, String typeId) {
            this.title = title;
            this.message = message;
            this.background = true;
            this.imageUrl = imageUrl;
            this.timeStamp = System.currentTimeMillis() + "";
            this.type = type;
            this.typeId = typeId;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public boolean isBackground() {
            return background;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public String getType() {
            return type;
        }

        public String getTypeId() {
            return typeId;
        }

        public boolean isAdminNotification() {
            return adminNotification;
        }

        public void setAdminNotification(boolean adminNotification) {
            this.adminNotification = adminNotification;
        }
    }

    public static class Notification {
        public String title;
        public String body;
        public String sound = "notification_sound.caf";

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isMutable_content() {
        return mutable_content;
    }

    public void setMutable_content(boolean mutable_content) {
        this.mutable_content = mutable_content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isContent_available() {
        return content_available;
    }

    public void setContent_available(boolean content_available) {
        this.content_available = content_available;
    }
}
