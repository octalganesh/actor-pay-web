package com.octal.actorPay.listener;

import com.google.gson.Gson;
import com.octal.actorPay.client.CMSClient;
import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.listener.events.AddMoneyIntoWallet;
import com.octal.actorPay.service.PaymentEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AddMoneyIntoWalletListener implements ApplicationListener<AddMoneyIntoWallet> {

    @Autowired
    private PaymentEmailService paymentEmailService;



    @Autowired
    private CMSClient cmsClient;

    @Autowired
    private NotificationClient notificationClient;


    @Override
    public void onApplicationEvent(AddMoneyIntoWallet event) {
        final UserDTO user = event.getUser();
        //Send notification on Money Add Into Wallet
        try {
            paymentEmailService.sendEmailOnAddMoneyIntoWallet(user.getEmail(),event.getWalletTransactionResponse(),event.getFcmRequest());
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
            if(user.getDeviceToken() != null){
                fcmUser.setFcmDeviceToken(user.getDeviceToken());
                fcmUser.setDeviceType(user.getDeviceType());
                request.setSystemUser(fcmUser);
//                request.setNotificationTypeId(user.getId());
                request.setImageUrl("");
                notificationClient.sendPushNotification(request);
                System.out.println("");
            }
        }


    }
}
