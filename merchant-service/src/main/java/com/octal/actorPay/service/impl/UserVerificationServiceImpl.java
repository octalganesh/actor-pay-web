package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.UserVerificationRepository;
import com.octal.actorPay.service.MerchantEmailService;
import com.octal.actorPay.service.UserVerificationService;
import com.octal.actorPay.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class UserVerificationServiceImpl implements UserVerificationService {


    @Autowired
    private UserVerificationRepository userOtpVerificationRepository;

    @Autowired
    private NotificationClient notificationClient;

    @Value("${forgot.password.time.in.second}")
    private String forgetPasswordOtpTimeInSec;

    @Value("${forgot.password.otp.resend.count}")
    private String forgetPasswordOtpAttemptsCounts;

    @Value("${user.phone.verification.otp.expire}")
    private String phoneVerificationOtpExpiration;

    @Value("${phone.verification.otp.resend.count}")
    private String phoneVerificationResendOTPCount;

    @Value("${user.activation.link.resend.count}")
    private String userActivationLinkResendCount;

    @Value("${user.activation.link.resend.time.in.second}")
    private String userActivationLinkResendTimeInSec;

    @Value("${phone.verification.resent.time.in.second}")
    private String phoneVerificationOtpTimeInSec;

    @Value("${user.activation.link.expiration.time.in.second}")
    private String userActivationLinkExpirationTimeInSec;

    @Autowired
    private MerchantEmailService merchantEmailService;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserOtpVerification generateUserOtp(User user, UserOtpVerification.Types types) {
        UserOtpVerification registrationToken = null;
        if (user != null) {
            Optional<UserOtpVerification> userOtpVerificationObj = userOtpVerificationRepository.findByTypeAndUserId(UserOtpVerification.Types.FORGOT_PASSWORD, user.getId());
            if (userOtpVerificationObj.isPresent()) {
                registrationToken = mapUserOtpObject(user, types, userOtpVerificationObj.get());
            } else {
                UserOtpVerification userOtpVerification = new UserOtpVerification();
                userOtpVerification.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                registrationToken = mapUserOtpObject(user, types, userOtpVerification);

            }

            return registrationToken;
        } else {
            throw new ActorPayException("verification.otp.creation.failed");
        }
    }

    @Override
    public UserOtpVerification resendUserOtp(User user, UserOtpVerification.Types types) {
        // TODO implement separate resend otp functionality
        return null;
    }

    private UserOtpVerification mapUserOtpObject(User user, UserOtpVerification.Types types, UserOtpVerification userOtpVerification) {
        // generate random otp
        Random random = new Random();
        // generate one random number with 6 digit
        String otp = String.valueOf(random.nextInt(100000));
        userOtpVerification.setActonCount(userOtpVerification.getActonCount() != null ? userOtpVerification.getActonCount() + 1 : 1);

        if (userOtpVerification.getType() != null && userOtpVerification.getType().equals(UserOtpVerification.Types.FORGOT_PASSWORD) && userOtpVerification.getUpdatedAt() != null) {
            // convert localDateTime to date to get epoch time
            Long currentTimeInSec = Date.from(LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toInstant()).getTime();
            Long actionTime = Date.from(userOtpVerification.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()).getTime();

            Long differenceOfTime = (currentTimeInSec - actionTime) / 1000;
            Long forgotTimeInSec = Long.valueOf(forgetPasswordOtpTimeInSec);

            if (differenceOfTime <= forgotTimeInSec && userOtpVerification.getActonCount() > Integer.parseInt(forgetPasswordOtpAttemptsCounts)) {
                throw new ActorPayException("Forgot Password was attempted recently. \n"
                        + "Subsequent attempts can be made after "
                        + TimeUnit.SECONDS.toMinutes(forgotTimeInSec) + " Minutes.");
            }

            if (differenceOfTime > Long.valueOf(forgetPasswordOtpTimeInSec)) {
                userOtpVerification.setActonCount(1);
            }

        }

//        userOtpVerification.setOtp(otp);
        String token = UUID.randomUUID().toString();
        userOtpVerification.setToken(token);
        userOtpVerification.setPhoneNumber(user.getContactNumber());
        userOtpVerification.setExtensionNumber(user.getExtensionNumber());
        userOtpVerification.setType(types);
        userOtpVerification.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
        userOtpVerification.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        userOtpVerification.setExpiredDateTime(userOtpVerification.getIssuedDateTime().plusDays(1));
        userOtpVerification.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
        userOtpVerification.setUserId(user.getId());
        userOtpVerification.setActive(true);
        UserOtpVerification savedRecord = userOtpVerificationRepository.save(userOtpVerification);

        merchantEmailService.sendEmailForForgetPasswordLink(user,token);
        return savedRecord;
    }


    @Override
    @Transactional
    public UserOtpVerification createUserVerificationToken(User user) {

        if (user != null) {
            UserOtpVerification userVerificationToken = new UserOtpVerification();
            // generate random token
            userVerificationToken.setToken(UUID.randomUUID().toString());
            userVerificationToken.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setType(UserOtpVerification.Types.NEW_USER);
            userVerificationToken.setExpiredDateTime(userVerificationToken.getIssuedDateTime().plusDays(1));
            userVerificationToken.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
            userVerificationToken.setUserId(user.getId());

            userVerificationToken.setActive(Boolean.TRUE);
            // in user verification we are not validation user with otp so we have added 0 as default value
            return userOtpVerificationRepository.save(userVerificationToken);

        } else {
            throw new ActorPayException("verification.token.creation.failed");
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserOtpVerification saveUserPhoneVerificationDetails(User user) {
        if (user != null) {
            UserOtpVerification userVerificationToken = null;
            Optional<UserOtpVerification> userOtpVerificationOptional = userOtpVerificationRepository.findByTypeAndUserId(UserOtpVerification.Types.PHONE_VERIFICATION, user.getId());
            if (!userOtpVerificationOptional.isPresent()) {
                userVerificationToken = new UserOtpVerification();
                userVerificationToken.setActonCount(userVerificationToken.getActonCount() != null ? userVerificationToken.getActonCount() + 1 : 1);
                userVerificationToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.setToken(null);
                userVerificationToken.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.setType(UserOtpVerification.Types.PHONE_VERIFICATION);
                userVerificationToken.setExpiredDateTime(userVerificationToken.getIssuedDateTime().plusHours(Long.parseLong(phoneVerificationOtpExpiration)));
                userVerificationToken.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
                userVerificationToken.setUserId(user.getId());
                userVerificationToken.setActive(true);
            } else {
                userVerificationToken = userOtpVerificationOptional.get();
                userVerificationToken.setActonCount(userVerificationToken.getActonCount() != null ? userVerificationToken.getActonCount() + 1 : 1);
                if (userVerificationToken.getType() != null && userVerificationToken.getType().equals(UserOtpVerification.Types.PHONE_VERIFICATION) && userVerificationToken.getUpdatedAt() != null) {
                    // convert localDateTime to date to get epoch time
                    Long currentTimeInSec = Date.from(LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toInstant()).getTime();
                    Long actionTime = Date.from(userVerificationToken.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()).getTime();

                    Long differenceOfTime = (currentTimeInSec - actionTime) / 1000;
                    Long forgotTimeInSec = Long.valueOf(phoneVerificationOtpTimeInSec);

                    if (differenceOfTime <= forgotTimeInSec && userVerificationToken.getActonCount() > Integer.parseInt(phoneVerificationResendOTPCount)) {
                        throw new ActorPayException("OTP request was attempted recently,"
                                + "Subsequent attempts can be made after "
                                + TimeUnit.SECONDS.toMinutes(forgotTimeInSec) + " Minutes.");
                    }

                    if (differenceOfTime > Long.valueOf(phoneVerificationOtpTimeInSec)) {
                        userVerificationToken.setActonCount(1);
                    }

                }

            }

            userVerificationToken.setOtp(CommonUtils.getRandomNumber(100000, 6));
            userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            return userOtpVerificationRepository.save(userVerificationToken);
        } else {
            throw new ActorPayException("verification.token.creation.failed");
        }
    }

    @Override
    @Transactional
    public UserOtpVerification generateVerificationLinkOnUserSignup(User user) {

        UserOtpVerification userVerificationToken = null;
        Optional<UserOtpVerification> userOtpVerification = userOtpVerificationRepository.findByTypeAndUserIdAndIsActiveIsTrue(UserOtpVerification.Types.NEW_USER, user.getId());
//        userV.ifPresent(userOtpVerification -> userOtpVerificationRepository.delete(userOtpVerification));

        if (!userOtpVerification.isPresent()) {
            userVerificationToken = new UserOtpVerification();
//            // generate random token
//            userVerificationToken.setToken(UUID.randomUUID().toString());
            userVerificationToken.setActonCount(0);
            userVerificationToken.setIssuedDateTime(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userVerificationToken.setType(UserOtpVerification.Types.NEW_USER);
            userVerificationToken.setExpiredDateTime(userVerificationToken.getIssuedDateTime().plusMinutes(TimeUnit.SECONDS.toMinutes(Integer.parseInt(userActivationLinkExpirationTimeInSec))));
            userVerificationToken.setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_PENDING);
            userVerificationToken.setUserId(user.getId());
            userVerificationToken.setActive(true);
            // in user verification we are not validation user with otp so we have added 0 as default value
            userVerificationToken.setOtp("0");

        } else {
            userVerificationToken = userOtpVerification.get();
            userVerificationToken.setActonCount(userVerificationToken.getActonCount() != null ? userVerificationToken.getActonCount() + 1 : 1);
            if (userVerificationToken.getType() != null && userVerificationToken.getType().equals(UserOtpVerification.Types.NEW_USER) && userVerificationToken.getUpdatedAt() != null) {
                // convert localDateTime to date to get epoch time
                Long currentTimeInSec = Date.from(LocalDateTime.now(ZoneOffset.UTC).atZone(ZoneId.systemDefault()).toInstant()).getTime();
                Long actionTime = Date.from(userVerificationToken.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant()).getTime();

                Long differenceOfTime = (currentTimeInSec - actionTime) / 1000;
                Long forgotTimeInSec = Long.valueOf(userActivationLinkResendTimeInSec);

                if (differenceOfTime <= forgotTimeInSec && userVerificationToken.getActonCount() > Integer.parseInt(userActivationLinkResendCount)) {
                    throw new ActorPayException("Resend activation link request was attempted recently,"
                            + "Subsequent attempts can be made after "
                            + TimeUnit.SECONDS.toMinutes(forgotTimeInSec) + " Minutes.");
                }

                if (differenceOfTime > Long.valueOf(userActivationLinkResendTimeInSec)) {
                    userVerificationToken.setActonCount(1);
                }
            }
        }

        userVerificationToken.setToken(UUID.randomUUID().toString());
        userVerificationToken.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        return userOtpVerificationRepository.save(userVerificationToken);

    }
}