package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MerchantWalletService {

    //    ApiResponse createWallet(WalletRequest walletRequest);
    ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception;

    ApiResponse getWalletBalance(String userType);

    ApiResponse transferMoney(WalletRequest walletRequest) throws Exception;

    void checkWalletStatus(String userId, String userType);

    ApiResponse searchWalletTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType,
                                        WalletFilterRequest filterRequest) throws Exception;

    ApiResponse requestMoney(RequestMoneyDTO requestMoneyDTO) throws Exception;
    ApiResponse cancelMoneyRequest(String requestId, String toUserId);

    ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception;

    ApiResponse getRequestMoney
    (RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize, String sortBy, boolean asc) throws Exception;

    ApiResponse acceptOrDeclineRequest(RequestMoneyDTO requestMoneyDTO) throws Exception;
//
//    ApiResponse getRequestMoney
//            (RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize,String sortBy, boolean asc) throws Exception;
//
//    ApiResponse acceptOrDeclineRequest(RequestMoneyDTO requestMoneyDTO) throws Exception;

    ApiResponse createOrder(OrderRequest orderRequest);

    ApiResponse readCSVFile(MultipartFile requestFile, String userId) throws IOException;

    ApiResponse getReadyPayrolls(String userId) throws Exception;

    ApiResponse processPayrolls(List<String> payrollIds) throws Exception;

    ApiResponse searchPayrollDetails(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType, WalletFilterRequest walletFilterRequest) throws Exception;
}
