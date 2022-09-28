package com.octal.actorPay.service;

import com.octal.actorPay.model.MailProperties;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;

@Component
public interface EmailService {
    void sendEmail(MailProperties mailProperties) throws MessagingException, IOException;
}