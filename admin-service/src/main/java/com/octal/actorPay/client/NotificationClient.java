package com.octal.actorPay.client;

import com.octal.actorPay.dto.EmailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
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
}