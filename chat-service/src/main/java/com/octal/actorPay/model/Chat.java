package com.octal.actorPay.model;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "chat")
@Data
public class Chat extends AbstractPersistable {

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "chat_room_id")
    private String chatRoomId;

    @Column(name = "message")
    private String message;

    @Column(name = "user_type")
    private String userType;
}
