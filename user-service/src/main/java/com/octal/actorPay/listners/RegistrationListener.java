package com.octal.actorPay.listners;

import com.google.gson.Gson;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.NotificationContentDTO;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDeviceDetails;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.feign.clients.CMSClient;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.listners.events.RegistrationCompleteEvent;
import com.octal.actorPay.service.UserEmailService;
import com.octal.actorPay.service.UserOtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.management.Notification;

/**
 * RegistrationListener - whenever a new user is created, it will receive a event,
 * once the event occurred, it will execute onApplicationEvent method and then it will send email to newly registered user
 */
@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private UserOtpService userOtpService;

    @Autowired
    private UserEmailService userEmailService;

    @Value("${user.activation.link.url}")
    private String activationLink;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        final User user = event.getUser();
        // userVerificationTokenService.generateVerificationLinkOnUserSignup(savedObject, P2PContants.EmailType.SINGUP.toString());
        UserOtpVerification userVerificationToken = userOtpService.generateVerificationLinkOnUserSignup(user);
        try {
            userEmailService.sendEmailOnSignUp(user,userVerificationToken.getToken());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
//        finally {
//            FcmUserNotificationDTO.Request request = new FcmUserNotificationDTO.Request();
//            request.setNotificationType(NotificationTypeEnum.SIGN_UP);
//            request.setNotificationTypeId(user.getId());
//            ApiResponse response = cmsClient.getNotificationContent("signUp");
//            if(response.getStatus().equalsIgnoreCase(String.valueOf(101))){
//                //Content Not Found!!
//            }else{
//                Gson gson = new Gson();
//                String jsonResponse = gson.toJson(response.getData());
//                NotificationContentDTO notificationContentDTO = gson.fromJson(jsonResponse,NotificationContentDTO.class);
//                request.setTitle(notificationContentDTO.getTitle());
//                request.setMessage(notificationContentDTO.getMessage());
//            }
//            request.setNotificationType(NotificationTypeEnum.SIGN_UP);
//            FcmUserNotificationDTO fcmUser = new FcmUserNotificationDTO();
//            fcmUser.setId(user.getId());
//            UserDeviceDetails userDeviceDetails = new UserDeviceDetails();
//            userDeviceDetails.setDeviceToken("dklYLxEOTL-OmZcfCtq5MI:APA91bEznpXEYnOjAB4izTBna7GVXOcl3d4qC_OO0vHfFsc3l7KXfqeh0hjjWyOWTkoJfoFQbdVGIT5uL-e5AYwRF994ArGiPK66D-K1zcgCGn2tgkd4HgDWLq7GdjJqXtZakEG933fe");
//            userDeviceDetails.setDeviceType("Android");
//            user.setUserDeviceDetails(userDeviceDetails);
//            if(user.getUserDeviceDetails() != null){
//                fcmUser.setFcmDeviceToken(user.getUserDeviceDetails().getDeviceToken());
//                fcmUser.setDeviceType(user.getUserDeviceDetails().getDeviceType());
//                request.setSystemUser(fcmUser);
////                request.setNotificationTypeId(user.getId());
//                request.setImageUrl("");
//                notificationClient.sendPushNotification(request);
//                System.out.println("");
//            }
//        }

    }
}