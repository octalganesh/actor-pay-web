package com.octal.actorPay.listners.events;

import com.amazonaws.services.mediaconvert.model.Order;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.OrderDetailsDTO;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Locale;

public class OrderEvent extends ApplicationEvent {

    private final OrderEventSource orderEventSource;
    private final FcmUserNotificationDTO.Request fcmRequest;
    private final User user;


    public OrderEvent(final OrderEventSource orderEventSource,FcmUserNotificationDTO.Request fcmRequest,User user) {
        super(orderEventSource);
        this.orderEventSource=orderEventSource;
        this.fcmRequest = fcmRequest;
        this.user = user;
    }

    public OrderEventSource getOrderEventSource() {
        return orderEventSource;
    }

    public FcmUserNotificationDTO.Request getFcmRequest() {
        return fcmRequest;
    }

    public User getUser() {
        return user;
    }
}
