package com.octal.actorPay.feign.clients;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.EmailDTO;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserNotificationListDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "notification-service", url = "http://localhost:8085/")
@Service
public interface NotificationClient {

    /**
     * This method  internally calls notification service to send email
     * @param emailPropertyDTO - EmailDTO object
     */
    @RequestMapping(value = "/notification/send/email",method = RequestMethod.POST)
    void sendEmailNotification(EmailDTO emailPropertyDTO);
    @RequestMapping(value = "/notification/send/push-notification",method = RequestMethod.POST)
    void sendPushNotification(FcmUserNotificationDTO.Request request);

    @RequestMapping(value = "/notification/notification-list",method = RequestMethod.POST)
    ApiResponse getUserNotificationList(@RequestBody UserNotificationListDTO.ListRequest request);
}