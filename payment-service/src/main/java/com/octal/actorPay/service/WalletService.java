package com.octal.actorPay.service;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.PayrollWalletDTO;
import com.octal.actorPay.dto.PayrollWalletHistoryDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.dto.request.CommonUserResponse;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.dto.request.WalletWithdrawRequest;
import com.octal.actorPay.model.entities.RequestMoney;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface WalletService {

    WalletDTO createUserWallet(WalletDTO walletDTO);
    String getWalletByUserIdAndUserType(String userId,String userType);
    WalletTransactionResponse addMoneyToWallet(WalletRequest request) throws Exception;

    PageItem<WalletTransactionDTO> searchWalletTransactions(WalletFilterRequest filterRequest, PagedItemInfo pagedInfo) throws Exception;

    WalletTransactionResponse transferMoney(WalletRequest walletRequest) throws Exception;
    WalletTransactionResponse withDrawMoney(WalletRequest request) throws Exception;
    WalletDTO getWalletBalanceByUserId(String userName,String userType) throws Exception;
    WalletDTO getWalletBalanceByUserId(String userId) throws Exception;
    ApiResponse getBeneficiaryDetails(String userIdentity);

    RequestMoneyDTO requestMoney(RequestMoneyDTO requestMoneyDTO) throws Exception;
    RequestMoneyDTO cancelMoneyRequest(String requestId, String toUserId) throws Exception;
    PageItem<RequestMoneyDTO> getRequestMoney(RequestMoneyFilter requestMoneyFilter, PagedItemInfo pagedInfo);
    RequestMoneyResponse acceptOrDecline(RequestMoneyDTO requestMoneyDTO) throws Exception;
    RequestMoneyDTO findRequestMoneyByUserIdAndToUserId(String userId, String toUserId);
//    RequestMoneyDTO findRequestMoneyByRequestId(String requestId);
    CommonUserResponse findUserType(String userIdentity);
    WalletTransactionResponse addMoneyToAdminWallet(WalletRequest request) throws Exception;

    long getTransactionCountByType(PurchaseType type);

    WalletTransactionResponse addRewardToWallet(WalletRequest request) throws Exception;

    PayrollWalletDTO readCSVFile(String userId, String userType, ByteArrayResource requestFile);

    PayrollWalletDTO getReadyPayrolls(String userId) throws Exception ;

    void processPayrolls(List<String> payrollIds) throws Exception;

    PageItem<PayrollWalletHistoryDTO> searchPayrollDetails(WalletFilterRequest walletFilterRequest, PagedItemInfo pagedInfo);

    List<WalletTransactionDTO> getAllWalletTransactionForReport(WalletFilterRequest walletFilterRequest) throws Exception;
}
