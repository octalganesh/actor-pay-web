package com.octal.actorPay.listners.events;

import com.octal.actorPay.dto.CancelOrderDTO;
import com.octal.actorPay.entities.OrderItem;

import java.util.List;

public class OrderEventSource {

    private CancelOrderDTO cancelOrderDTO;

    private List<OrderItem> cancelledItems;

    private String orderNo;

    private String eventType;
;
    public OrderEventSource(CancelOrderDTO cancelOrderDTO,
                            List<OrderItem> cancelledItems, String orderNo,String eventType) {
        this.cancelledItems = cancelledItems;
        this.cancelOrderDTO = cancelOrderDTO;
        this.orderNo = orderNo;
        this.eventType=eventType;
    }

    public OrderEventSource(String orderNo,String eventType) {
        this.orderNo = orderNo;
        this.eventType=eventType;
    }


    public CancelOrderDTO getCancelOrderDTO() {
        return cancelOrderDTO;
    }

    public List<OrderItem> getCancelledItems() {
        return cancelledItems;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getEventType() {
        return eventType;
    }
}
