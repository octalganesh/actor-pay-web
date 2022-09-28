package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.PaymentWalletClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.MerchantWalletService;
import com.octal.actorPay.utils.MerchantFeignHelper;
import com.octal.actorPay.utils.PercentageCalculateManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class MerchantWalletServiceImpl implements MerchantWalletService {

    private PaymentWalletClient paymentWalletClient;
    private MerchantFeignHelper merchantFeignHelper;
    private MerchantService merchantService;
    private PercentageCalculateManager percentageCalculateManager;


    public MerchantWalletServiceImpl(PaymentWalletClient paymentWalletClient,
                                     PercentageCalculateManager percentageCalculateManager,
                                     MerchantFeignHelper merchantFeignHelper, MerchantService merchantService) {
        this.paymentWalletClient = paymentWalletClient;
        this.percentageCalculateManager = percentageCalculateManager;
        this.merchantFeignHelper = merchantFeignHelper;
        this.merchantService = merchantService;
    }

    @Override
    public ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception {

        SystemConfigurationDTO walletCommission = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.WALLET_COMMISSION);
        walletRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        SystemConfigurationDTO systemConfigurationDTO = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        walletRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ResponseEntity<ApiResponse> responseEntity = paymentWalletClient.addMoneyToWallet(walletRequest);
        return responseEntity.getBody();
    }

    //
    @Override
    public ApiResponse transferMoney(WalletRequest walletRequest) throws Exception {
        ApiResponse apiResponse = null;
        SystemConfigurationDTO adminConfig = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (adminConfig == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(adminConfig.getParamValue());
        apiResponse = paymentWalletClient.transferMoney(walletRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse getWalletBalance(String userType) {
        ApiResponse apiResponse =
                paymentWalletClient.getWalletBalance(userType);
        return apiResponse;
    }

    @Override
    public void checkWalletStatus(String userId, String userType) {
        ApiResponse apiResponse = paymentWalletClient.checkWalletStatus(userId, userType);
        if (apiResponse == null) {
            throw new RuntimeException(String.format("Invalid User wallet: Error while creating new Wallet for user %s ", userId));
        }
    }

    @Override
    public ApiResponse searchWalletTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc,
                                               String userType, WalletFilterRequest filterRequest) throws Exception {

        SystemConfigurationDTO systemConfigurationDTO = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Admin Id is not configured in the Global Settings");
        }
        filterRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        ResponseEntity<ApiResponse> responseEntity =
                paymentWalletClient.searchWalletTransaction(pageNo, pageSize, sortBy, asc, userType, filterRequest);
        ApiResponse apiResponse = responseEntity.getBody();
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

    @Transactional(rollbackFor = {ActorPayException.class})
    @Override
    public ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception {
        SystemConfigurationDTO walletCommission = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.WALLET_COMMISSION);
        SystemConfigurationDTO systemConfigurationDTO = merchantFeignHelper.getGlobalConfigByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new ActorPayException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        withdrawRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        if (walletCommission != null)
            withdrawRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        ApiResponse apiResponse = paymentWalletClient.withdraw(withdrawRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse readCSVFile(MultipartFile requestFile, String userId) throws IOException {
        return paymentWalletClient.readCSVFile(userId, CommonConstant.USER_TYPE_MERCHANT, new ByteArrayResource(requestFile.getBytes()));
    }

    @Override
    public ApiResponse getReadyPayrolls(String userId) throws Exception {
        return paymentWalletClient.getReadyPayrolls(userId);
    }

    @Override
    public ApiResponse processPayrolls(List<String> payrollIds) throws Exception {
        return paymentWalletClient.processPayrolls(payrollIds);
    }

    @Override
    public ApiResponse searchPayrollDetails(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType, WalletFilterRequest filterRequest) throws Exception {
        return paymentWalletClient.searchPayrollDetails(pageNo, pageSize, sortBy, asc, userType, filterRequest);
    }
}
