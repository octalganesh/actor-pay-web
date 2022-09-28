package com.octal.actorPay.service;

import com.octal.actorPay.dto.DisputeItemDTO;
import com.octal.actorPay.entities.DisputeItem;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface UserEmailService {

    void sendEmailOnNewUserRegistrationByAdmin(User user, String activationToken) throws Exception;

    void sendEmailOnSignUp(User user, String activationToken);

    void sendEmailForForgetPasswordLink(User user, String token);

    void sendEmailOnDisputeAlarm(DisputeItem disputeItem);
}