package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.dto.EmailDTO;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.DefaultEmailService;
import com.octal.actorPay.service.EmailService;
import com.octal.actorPay.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private NotificationClient notificationClient;

    @Value("${user.activation.link.url}")
    private String activationLink;

    @Override
    public void sendEmailForForgetPasswordLink(User user, String token) {

    }

    @Override
    public void sendEmailOnNewUserRegistrationByAdmin(User user, String activationToken, String password) {
        EmailDTO mail = new EmailDTO();
        mail.setMailTo(user.getEmail());
        mail.setSubject("[ActorPay] Welcome to ActorPay");
        mail.setTemplateName("ADD_NEW_USER_BY_ADMIN");
        Map<String, Object> model = new HashMap<>();
        // TODO added below properties for testing purpose only, will replace once we got proper template
        model.put("name", user.getFirstName()+" "+user.getLastName());
        model.put("password", password);
        model.put("activationLink", activationLink.replace("<token>",activationToken));
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }


    @Override
    public void sendEmailOnNewUserRegistrationByAdmin(User user, String activationToken) {

    }

    @Override
    public void sendEmailOnSignUp(User user, String activationToken) {
        EmailDTO mail = new EmailDTO();
        // TODO we have added hardcode for testing purpose only
        mail.setMailTo(user.getEmail());
        mail.setSubject("[ActorPay] Welcome to ActorPay");
        // this method will be removed later
        mail.setTemplateName("CUSTOMER_SIGNUP");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", user.getFirstName()+" "+user.getLastName());
        model.put("activationLink", activationLink.replace("<token>",activationToken));
        model.put("location", "India");
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }
}