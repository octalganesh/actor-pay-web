package com.octal.actorPay.controller;

import com.octal.actorPay.dto.AuthUserDTO;
import com.octal.actorPay.dto.ChatMessage;
import com.octal.actorPay.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import static java.lang.String.format;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/{roomId}/sendMessage")
    public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info(roomId + " Chat Message received is " + chatMessage.getContent());
        chatMessage.setSender((String)headerAccessor.getSessionAttributes().get("name"));
        chatService.saveChat(roomId, chatMessage);
        messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);
    }

    @MessageMapping("/chat/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", roomId);
        String token =  headerAccessor.getFirstNativeHeader("Authorization");
        AuthUserDTO user = chatService.resolveToken(token);
        chatMessage.setSender(user.getUsername());
        chatService.saveChatRoom(roomId, chatMessage);
        if (currentRoomId != null) {
            ChatMessage leaveMessage = new ChatMessage();
            leaveMessage.setMessageType(ChatMessage.MessageType.LEAVE);
     //       leaveMessage.setSender(user.getFirstName() + " " + user.getLastName());
            messagingTemplate.convertAndSend(format("/chat-room/%s", currentRoomId), leaveMessage);
            chatService.deactivateChatRoom(roomId);
        }
        headerAccessor.getSessionAttributes().put("name", user.getUsername());
        messagingTemplate.convertAndSend(format("/chat-room/%s", roomId), chatMessage);
    }

}

