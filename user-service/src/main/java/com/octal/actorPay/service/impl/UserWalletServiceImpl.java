package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.feign.clients.PaymentClient;
import com.octal.actorPay.service.UserService;
import com.octal.actorPay.service.UserWalletService;
import com.octal.actorPay.utils.PercentageCalculateManager;
import com.octal.actorPay.utils.UserFeignHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserWalletServiceImpl implements UserWalletService {

    private PaymentClient paymentWalletClient;
    private PercentageCalculateManager percentageCalculateManager;
    private UserFeignHelper userFeignHelper;
    private UserService userService;

    public UserWalletServiceImpl(PaymentClient paymentWalletClient,
                                 PercentageCalculateManager percentageCalculateManager,
                                 UserFeignHelper userFeignHelper, UserService userService) {
        this.paymentWalletClient = paymentWalletClient;
        this.percentageCalculateManager = percentageCalculateManager;
        this.userFeignHelper = userFeignHelper;
        this.userService = userService;
    }

    @Override
    public ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception {
        SystemConfigurationDTO walletCommission = userFeignHelper.getGlobalConfigByKey(CommonConstant.WALLET_COMMISSION);
        walletRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        SystemConfigurationDTO systemConfigurationDTO = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        walletRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ResponseEntity<ApiResponse> responseEntity = paymentWalletClient.addMoneyToWallet(walletRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse addRewardToWallet(WalletRequest walletRequest) {
        try {
            SystemConfigurationDTO systemConfigurationDTO = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
            walletRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        } catch (Exception e){}
        ResponseEntity<ApiResponse> responseEntity = paymentWalletClient.addRewardToWallet(walletRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse transferMoney(WalletRequest walletRequest) throws Exception {
        ApiResponse apiResponse = null;
        SystemConfigurationDTO adminConfig = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (adminConfig == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(adminConfig.getParamValue());
        apiResponse = paymentWalletClient.transferMoney(walletRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse searchWalletTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc,
                                               String userType, WalletFilterRequest filterRequest) throws Exception {

        SystemConfigurationDTO systemConfigurationDTO = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        filterRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        ResponseEntity<ApiResponse> responseEntity =
                paymentWalletClient.searchWalletTransaction(pageNo, pageSize, sortBy, asc, userType, filterRequest);
        ApiResponse apiResponse = responseEntity.getBody();
        return apiResponse;
    }

    @Override
    public ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception {
        SystemConfigurationDTO walletCommission = userFeignHelper.getGlobalConfigByKey(CommonConstant.WALLET_COMMISSION);
        SystemConfigurationDTO systemConfigurationDTO = userFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        withdrawRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        if (walletCommission != null)
            withdrawRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        ResponseEntity<ApiResponse> responseEntity =
                paymentWalletClient.withdraw(withdrawRequest);
        return responseEntity.getBody();
    }

//    @Override
//    public ApiResponse getWalletBalance(String userType) {
//        ApiResponse apiResponse =
//                paymentWalletClient.getWalletBalance(userType);
//        return apiResponse;
//    }

    @Override
    public ApiResponse getWalletBalance(String userName, String userType) {
        ApiResponse apiResponse =
                paymentWalletClient.getWalletBalance(userName, userType);
        return apiResponse;
    }

    @Override
    public ApiResponse requestMoney(RequestMoneyDTO requestMoneyDTO) throws Exception {
        ApiResponse apiResponse = paymentWalletClient.requestMoney(requestMoneyDTO);
        return apiResponse;
    }

    @Override
    public ApiResponse getRequestMoney(RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize, String sortBy, boolean asc) throws Exception {
        ApiResponse apiResponse = paymentWalletClient.getRequestMoney(requestMoneyFilter, pageNo, pageSize, sortBy, asc);
        return apiResponse;
    }

    @Override
    public ApiResponse acceptOrDeclineRequest(RequestMoneyDTO requestMoneyDTO) throws Exception {
        ApiResponse apiResponse = paymentWalletClient.acceptOrDeclineRequest(requestMoneyDTO);
        return apiResponse;
    }

    @Override
    public ApiResponse cancelMoneyRequest(String requestId, String toUserId) {
        ApiResponse apiResponse = paymentWalletClient.cancelMoneyRequest(requestId, toUserId);
        return apiResponse;
    }

    @Override
    public ApiResponse createOrder(OrderRequest orderRequest) {
        ApiResponse apiResponse = paymentWalletClient.createOrder(orderRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse verify(PaymentGatewayResponse paymentGatewayResponse) {
        ApiResponse apiResponse = paymentWalletClient.verify(paymentGatewayResponse);
        return apiResponse;
    }

    @Override
    public void createCustomerWallet(WalletRequest walletBalanceRequest) {
        paymentWalletClient.createWallet(walletBalanceRequest);
    }


}
