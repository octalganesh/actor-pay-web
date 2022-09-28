package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.service.PaymentEmailService;
import com.octal.actorPay.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentEmailServiceImpl implements PaymentEmailService {

    @Autowired
    private NotificationClient notificationClient;

    @Override
    public void sendEmailOnAddMoneyIntoWallet(String email, WalletTransactionResponse walletTransactionResponse, FcmUserNotificationDTO.Request request) {
        EmailDTO mail = new EmailDTO();
        // TODO we have added hardcode for testing purpose only
        mail.setMailTo(email);
        // Read from config server
        Map<String, Object> model = new HashMap<>();
        if(request.getNotificationType().equals(NotificationTypeEnum.ADD_MONEY)){
            model.put("title","Money add into Wallet Successfully");
            mail.setTemplateName("ADD_MONEY");
        }else if(request.getNotificationType().equals(NotificationTypeEnum.CREDIT_INTO_WALLET)){
            model.put("title","Money received into Wallet Successfully");
            mail.setTemplateName("CREDIT_INTO_WALLET");
        }else if(request.getNotificationType().equals(NotificationTypeEnum.DEBIT_FROM_WALLET)){
            model.put("title","Money transfer from Wallet Successfully");
            mail.setTemplateName("DEBIT_FROM_WALLET");
        }else{
            model.put("title","Payment");
            mail.setTemplateName("PAYMENT");
        }
        model.put("name", walletTransactionResponse.getCustomerName());
        model.put("amount", walletTransactionResponse.getTransferredAmount());
        model.put("time", TextUtils.getStringDate(new Date(),"MMMM dd, yyyy"));
        model.put("method", walletTransactionResponse.getModeOfTransaction());
        model.put("transactionId",walletTransactionResponse.getParentTransactionId());
        model.put("sign", "ActorPay Team");
        mail.setProps(model);
        notificationClient.sendEmailNotification(mail);
    }
}
