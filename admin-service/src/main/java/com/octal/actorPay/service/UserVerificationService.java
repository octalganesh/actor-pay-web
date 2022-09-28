package com.octal.actorPay.service;

import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import org.springframework.stereotype.Component;

@Component
public interface UserVerificationService {


    UserOtpVerification generateUserOtp(User user, UserOtpVerification.Types types);

    UserOtpVerification resendUserOtp(User user, UserOtpVerification.Types types);

    UserOtpVerification createUserVerificationToken(User user);
}