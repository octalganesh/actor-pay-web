package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FcmUserNotificationDTO {


    public static class Request{
        private String title;
        private String message;
        private String imageUrl;
        NotificationTypeEnum notificationType;
        String notificationTypeId;
        FcmUserNotificationDTO systemUser;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public NotificationTypeEnum getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(NotificationTypeEnum notificationType) {
            this.notificationType = notificationType;
        }

        public String getNotificationTypeId() {
            return notificationTypeId;
        }

        public void setNotificationTypeId(String notificationTypeId) {
            this.notificationTypeId = notificationTypeId;
        }

        public FcmUserNotificationDTO getSystemUser() {
            return systemUser;
        }

        public void setSystemUser(FcmUserNotificationDTO systemUser) {
            this.systemUser = systemUser;
        }
    }


    private String id;
    private String deviceType;
    private String fcmDeviceToken;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getFcmDeviceToken() {
        return fcmDeviceToken;
    }

    public void setFcmDeviceToken(String fcmDeviceToken) {
        this.fcmDeviceToken = fcmDeviceToken;
    }
}