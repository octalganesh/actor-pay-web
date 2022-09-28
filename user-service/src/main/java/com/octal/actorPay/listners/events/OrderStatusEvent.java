package com.octal.actorPay.listners.events;

import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

public class OrderStatusEvent extends ApplicationEvent {

    private final FcmUserNotificationDTO.Request fcmRequest;
    private final User user;


    public OrderStatusEvent(final FcmUserNotificationDTO.Request fcmRequest, User user) {
        super(fcmRequest);
        this.fcmRequest = fcmRequest;
        this.user = user;
    }

    public FcmUserNotificationDTO.Request getFcmRequest() {
        return fcmRequest;
    }

    public User getUser() {
        return user;
    }
}
