package com.octal.actorPay.service;

import com.octal.actorPay.dto.CommonUserDTO;
import com.octal.actorPay.dto.FcmUserNotificationDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface PaymentEmailService {


    void sendEmailOnAddMoneyIntoWallet(String email, WalletTransactionResponse walletTransactionResponse, FcmUserNotificationDTO.Request request);

}
