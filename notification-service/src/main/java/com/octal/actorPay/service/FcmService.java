package com.octal.actorPay.service;

import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserNotificationListDTO;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.model.FcmNotificationBean;
import org.springframework.stereotype.Component;

@Component
public interface FcmService {
    public void sendNotificationToUser(String title, String message,
                                       String imageUrl,
                                       NotificationTypeEnum notificationType, String notificationTypeId,
                                       FcmUserNotificationDTO systemUser);

    UserNotificationListDTO getUserNotification(UserNotificationListDTO.ListRequest request);

}
