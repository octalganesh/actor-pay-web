package com.octal.actorPay.service;

import com.octal.actorPay.entities.User;

public interface MerchantEmailService extends DefaultEmailService<User> {

    void sendEmailForForgetPasswordLink(User user, String token);

    void sendEmailOnNewUserRegistrationByMerchant(User user,String activationToken, String Password);


}
