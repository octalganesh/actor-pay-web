package com.octal.actorPay.model;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chat_room")
@Data
public class ChatRoom extends AbstractPersistable {

    @Column(name = "order_id")
    private String orderId;

    @Column(name ="user_name")
    private String userName;

    @Column(name = "type")
    private String type;
}
