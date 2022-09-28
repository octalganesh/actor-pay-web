package com.octal.actorPay.service;


import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.entities.User;

public interface UserPGService {

    ApiResponse refund(RefundRequest refundRequest) throws Exception;

    ApiResponse savePgDetails(PgDetailsDTO pgDetailsDTO);

    ApiResponse createQrCode(QrCodeCreateRequest request);

    ApiResponse getPGPaymentDetails(String paymentTypeId);

    ApiResponse createFundAccount(BankAccountRequest addBankRequest);

    ApiResponse createContact(ContactRequest contactRequest);

    ApiResponse getUserFundAccounts(String userId);

    ApiResponse getUsersSelfFundAccounts(String userId);

    ApiResponse getUsersBeneficiaryAccounts(String userId);

    ApiResponse getUserFundAccountByFundAccountId(String userId,String fundAccountId);
    ApiResponse activeOrDeActiveAccount(DeactivateRequest deactivateRequest);

    ApiResponse createPayout(PayoutRequest payoutRequest);

    ApiResponse setPrimaryOrSecondaryAccount(UpdateFundAccountRequest deactivateRequest);

    ApiResponse setSelfAccount(UpdateFundAccountRequest payoutRequest);

    void qrCodePaymentCredit(QrCodeCreditRequest creditRequest);

    void sendEmailOnAddMoneyIntoWallet(String email, WalletTransactionResponse walletTransactionResponse, FcmUserNotificationDTO.Request request);
}
