package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserNotificationListDTO;
import com.octal.actorPay.model.MailProperties;
import com.octal.actorPay.service.EmailService;
import com.octal.actorPay.service.FcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private FcmService fcmService;

    @PostMapping("/send/email")
    public ResponseEntity<?> sendEmail(@RequestBody MailProperties MailProperties) throws IOException, MessagingException {

        emailService.sendEmail(MailProperties);
        return new ResponseEntity<>(new ApiResponse("Email sent successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/send/push-notification")
    public ResponseEntity<?> sendPushNotification(@RequestBody FcmUserNotificationDTO.Request request) throws IOException, MessagingException {

        fcmService.sendNotificationToUser(request.getTitle(), request.getMessage(), request.getImageUrl(), request.getNotificationType(), request.getNotificationTypeId(), request.getSystemUser());
        return new ResponseEntity<>(new ApiResponse("Notification sent successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @RequestMapping(value = "/notification-list", method = RequestMethod.POST)
    public ResponseEntity<?> getUserNotificationList(@RequestBody UserNotificationListDTO.ListRequest request) {
        return new ResponseEntity<>(new ApiResponse("Notification sent successfully", fcmService.getUserNotification(request),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}