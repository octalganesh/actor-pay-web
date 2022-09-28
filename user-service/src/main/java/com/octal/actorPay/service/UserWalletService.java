package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserWalletService {

    ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception;

    ApiResponse addRewardToWallet(WalletRequest walletRequest);

    ApiResponse transferMoney(WalletRequest walletRequest) throws Exception;

    ApiResponse searchWalletTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType,
                                        WalletFilterRequest filterRequest) throws Exception;

    ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception;

//    ApiResponse getWalletBalance(String userType);
    ApiResponse getWalletBalance(String userName,String userType);
    ApiResponse requestMoney(RequestMoneyDTO requestMoneyDTO) throws Exception;

    ApiResponse getRequestMoney
            (RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize, String sortBy, boolean asc) throws Exception;

    ApiResponse acceptOrDeclineRequest(RequestMoneyDTO requestMoneyDTO) throws Exception;

    ApiResponse cancelMoneyRequest(String requestId, String toUserId);

    ApiResponse createOrder(OrderRequest orderRequest);

    ApiResponse verify(PaymentGatewayResponse paymentGatewayResponse);

    void createCustomerWallet(WalletRequest walletBalanceRequest);

}
