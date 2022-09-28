package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.enums.NotificationTypeEnum;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.feign.clients.PaymentClient;
import com.octal.actorPay.service.UserPGService;
import com.octal.actorPay.utils.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserPGServiceImpl implements UserPGService {

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private NotificationClient notificationClient;

    @Override
    public ApiResponse refund(RefundRequest refundRequest) throws Exception {
        ApiResponse apiResponse = paymentClient.doRefund(refundRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse savePgDetails(PgDetailsDTO pgDetailsDTO) {
        ApiResponse apiResponse = paymentClient.savePgDetails(pgDetailsDTO);
        return apiResponse;
    }

    @Override
    public ApiResponse createQrCode(QrCodeCreateRequest request) {
        ApiResponse apiResponse = paymentClient.createQrCode(request);
        return apiResponse;
    }

    @Override
    public ApiResponse getPGPaymentDetails(String paymentTypeId) {
        ApiResponse apiResponse = paymentClient.getPGPaymentDetails(paymentTypeId);
        return apiResponse;
    }

    @Override
    public ApiResponse createFundAccount(BankAccountRequest addBankRequest) {
        ApiResponse apiResponse = paymentClient.createFundAccount(addBankRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse createContact(ContactRequest contactRequest) {
        ApiResponse apiResponse = paymentClient.createContact(contactRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse getUserFundAccounts(String userId) {
        ApiResponse apiResponse = paymentClient.getUserFundAccounts(userId);
        return apiResponse;
    }

    @Override
    public ApiResponse getUsersSelfFundAccounts(String userId) {
        ApiResponse apiResponse = paymentClient.getUsersSelfFundAccounts(userId);
        return apiResponse;
    }

    @Override
    public ApiResponse getUsersBeneficiaryAccounts(String userId) {
        ApiResponse apiResponse = paymentClient.getUsersBeneficiaryAccounts(userId);
        return apiResponse;
    }

    @Override
    public ApiResponse getUserFundAccountByFundAccountId(String userId,String fundAccountId) {
        ApiResponse apiResponse = paymentClient.getUserFundAccountByFundAccountId(userId,fundAccountId);
        return apiResponse;
    }

    @Override
    public ApiResponse activeOrDeActiveAccount(DeactivateRequest deactivateRequest) {
        ApiResponse apiResponse = paymentClient.activeOrDeActiveAccount(deactivateRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse createPayout(PayoutRequest payoutRequest) {
        ApiResponse apiResponse = paymentClient.createPayout(payoutRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse setPrimaryOrSecondaryAccount(UpdateFundAccountRequest request) {
        ApiResponse apiResponse = paymentClient.setPrimaryOrSecondaryAccount(request);
        return apiResponse;
    }

    @Override
    public ApiResponse setSelfAccount(UpdateFundAccountRequest request) {
        ApiResponse apiResponse = paymentClient.setSelfAccount(request);
        return apiResponse;
    }

    @Override
    public void qrCodePaymentCredit(QrCodeCreditRequest creditRequest) {
        paymentClient.qrCodePaymentCredit(creditRequest);
    }

    @Override
    public void sendEmailOnAddMoneyIntoWallet(String email, WalletTransactionResponse walletTransactionResponse, FcmUserNotificationDTO.Request request) {
        EmailDTO mail = new EmailDTO();
        // TODO we have added hardcode for testing purpose only
        mail.setMailTo(email);
        // Read from config server
        Map<String, Object> model = new HashMap<>();
        if(request.getNotificationType().equals(NotificationTypeEnum.WITHDRAW)){
            model.put("title","Money withdraw successfully");
            mail.setTemplateName("WITHDRAW");
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
