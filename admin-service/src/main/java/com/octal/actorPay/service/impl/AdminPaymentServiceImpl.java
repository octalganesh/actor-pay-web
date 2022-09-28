package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.AdminPaymentClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.service.AdminPaymentService;
import com.octal.actorPay.service.SystemConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AdminPaymentServiceImpl implements AdminPaymentService {

    private AdminPaymentClient adminPaymentClient;

    private SystemConfigurationService systemConfigurationService;

    public AdminPaymentServiceImpl(AdminPaymentClient adminPaymentClient, SystemConfigurationService systemConfigurationService) {
        this.adminPaymentClient = adminPaymentClient;
        this.systemConfigurationService = systemConfigurationService;

    }

    @Override
    public ApiResponse searchWalletTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc,
                                               String userType, WalletFilterRequest filterRequest) {
        ResponseEntity<ApiResponse> responseEntity =
                adminPaymentClient.searchWalletTransaction(pageNo, pageSize, sortBy, asc, userType, filterRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse searchPayrollDetails(Integer pageNo, Integer pageSize, String sortBy, boolean asc,
                                               String userType, WalletFilterRequest filterRequest) {
        ResponseEntity<ApiResponse> responseEntity =
                adminPaymentClient.searchPayrollDetails(pageNo, pageSize, sortBy, asc, userType, filterRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse getWalletBalance(String userId) {
        ApiResponse apiResponse =
                adminPaymentClient.getWalletBalance(userId);
        return apiResponse;
    }

    @Override
    public ApiResponse getRequestMoney(RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize,
                                       String sortBy, boolean asc) {
        ApiResponse apiResponse = adminPaymentClient.getRequestMoney(requestMoneyFilter, pageNo, pageSize, sortBy, asc);
        return apiResponse;
    }

    @Override
    public ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception {
        SystemConfigurationDTO walletCommission = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.WALLET_COMMISSION);
        walletRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        SystemConfigurationDTO systemConfigurationDTO = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        walletRequest.setUserType(CommonConstant.USER_TYPE_ADMIN);
        ResponseEntity<ApiResponse> responseEntity = adminPaymentClient.addMoneyToWallet(walletRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse transferMoney(WalletRequest walletRequest) throws Exception {
        ApiResponse apiResponse = null;
        SystemConfigurationDTO adminConfig = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.ADMIN_ID);
        if (adminConfig == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        walletRequest.setAdminUserId(adminConfig.getParamValue());
        apiResponse = adminPaymentClient.transferMoney(walletRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception {
        SystemConfigurationDTO walletCommission = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.WALLET_COMMISSION);
        SystemConfigurationDTO systemConfigurationDTO = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.ADMIN_ID);
        if (systemConfigurationDTO == null) {
            throw new RuntimeException("Wallet Commission Percentage is not configured in the Global Settings");
        }
        withdrawRequest.setAdminUserId(systemConfigurationDTO.getParamValue());
        if (walletCommission != null)
            withdrawRequest.setCommissionPercentage(Double.parseDouble(walletCommission.getParamValue()));
        ResponseEntity<ApiResponse> responseEntity =
                adminPaymentClient.withdraw(withdrawRequest);
        return responseEntity.getBody();
    }

    @Override
    public ApiResponse createContact(ContactRequest contactRequest) throws Exception {
        ApiResponse apiResponse = adminPaymentClient.createContact(contactRequest);
        return apiResponse;
    }

    @Override
    public ApiResponse doRefund(RefundRequest refundRequest) {
        ApiResponse apiResponse = adminPaymentClient.doRefund(refundRequest);
        return apiResponse;
    }
}
