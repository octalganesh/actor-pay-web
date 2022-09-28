package com.octal.actorPay.dto;

import lombok.Data;


@Data
public class ChatMessage {
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

    private MessageType messageType;
    private String content;
    private String sender;
}


