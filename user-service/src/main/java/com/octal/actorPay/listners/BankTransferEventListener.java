package com.octal.actorPay.listners;

import com.google.gson.Gson;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.NotificationContentDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDeviceDetails;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.feign.clients.CMSClient;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.listners.events.BankTransferEvent;
import com.octal.actorPay.listners.events.RegistrationCompleteEvent;
import com.octal.actorPay.service.UserEmailService;
import com.octal.actorPay.service.UserOtpService;
import com.octal.actorPay.service.UserPGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * RegistrationListener - whenever a new user is created, it will receive a event,
 * once the event occurred, it will execute onApplicationEvent method and then it will send email to newly registered user
 */
@Component
public class BankTransferEventListener implements ApplicationListener<BankTransferEvent> {

    @Autowired
    private Environment env;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private UserPGService userPGService;

    @Autowired
    private UserOtpService userOtpService;

    @Autowired
    private UserEmailService userEmailService;

    @Value("${user.activation.link.url}")
    private String activationLink;

    @Override
    public void onApplicationEvent(BankTransferEvent event) {
        final User user = event.getUser();
        //Send notification on Money Add Into Wallet
        try {
            userPGService.sendEmailOnAddMoneyIntoWallet(user.getEmail(),event.getWalletTransactionResponse(),event.getFcmRequest());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            FcmUserNotificationDTO.Request request = event.getFcmRequest();
            ApiResponse response = cmsClient.getNotificationContent(request.getNotificationType().toString());
            if(response.getStatus().equalsIgnoreCase(String.valueOf(101))){
                //Content Not Found!!
                System.out.println("Content Not Found!!");
            }else{
                Gson gson = new Gson();
                String jsonResponse = gson.toJson(response.getData());
                NotificationContentDTO notificationContentDTO = gson.fromJson(jsonResponse,NotificationContentDTO.class);
                request.setTitle(notificationContentDTO.getTitle());
                request.setMessage(notificationContentDTO.getMessage());
            }

            FcmUserNotificationDTO fcmUser = new FcmUserNotificationDTO();
            fcmUser.setId(user.getId());
            if(user.getUserDeviceDetails() != null){
                fcmUser.setFcmDeviceToken(user.getUserDeviceDetails().getDeviceToken());
                fcmUser.setDeviceType(user.getUserDeviceDetails().getDeviceType());
                request.setSystemUser(fcmUser);
//                request.setNotificationTypeId(user.getId());
                request.setImageUrl("");
                notificationClient.sendPushNotification(request);
                System.out.println("");
            }
        }


    }
}