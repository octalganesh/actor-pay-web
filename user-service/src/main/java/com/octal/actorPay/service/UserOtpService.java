package com.octal.actorPay.service;

import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface UserOtpService {


    UserOtpVerification generateOtpRequestToResetUserPassword(User user, UserOtpVerification.Types types);

    UserOtpVerification resendUserOtp(User user, UserOtpVerification.Types types);

    UserOtpVerification generateVerificationLinkOnUserSignup(User user);

    UserOtpVerification saveUserPhoneVerificationDetails(User user);
}