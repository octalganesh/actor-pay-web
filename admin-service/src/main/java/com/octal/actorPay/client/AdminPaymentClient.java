package com.octal.actorPay.client;

import com.octal.actorPay.config.FeignConfig;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient(name = "payment-service", url = "http://localhost:8096/", configuration = FeignConfig.class)
@Service
public interface AdminPaymentClient {

    @RequestMapping(value = "/v1/wallet/create", method = RequestMethod.POST)
    void createWallet(@RequestBody @Valid WalletRequest walletBalanceRequest);

    @RequestMapping(value = "/v1/wallet/addMoney", method = RequestMethod.POST)
    ResponseEntity<ApiResponse> addMoneyToWallet(@RequestBody WalletRequest walletRequest);

    @PostMapping(value = "/v1/wallet/withdraw")
    ResponseEntity<ApiResponse> withdraw(@RequestBody WalletRequest withdrawRequest);

    @PostMapping(value = "/v1/wallet/transfer")
    ApiResponse transferMoney(@RequestBody WalletRequest walletRequest);

    @PostMapping(value = "/v1/wallet/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTransaction(
            @RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
            @RequestParam(name="asc",defaultValue = "false") boolean asc,
            @RequestParam(name = "userType",defaultValue = "admin",required = false) String userType,
            @RequestBody WalletFilterRequest walletFilterRequest);

    @PostMapping(value = "/v1/wallet/payroll/list/paged")
    public ResponseEntity<ApiResponse> searchPayrollDetails(
            @RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
            @RequestParam(name="asc",defaultValue = "false") boolean asc,
            @RequestParam(name = "userType",defaultValue = "admin",required = false) String userType,
            @RequestBody WalletFilterRequest walletFilterRequest);

    @GetMapping("/v1/wallet/admin/balance/{userId}")
    ApiResponse getWalletBalance(@PathVariable(name = "userId") String userId);

    @PostMapping("/v1/wallet/requestMoney/list/paged")
    ApiResponse getRequestMoney(@RequestBody RequestMoneyFilter requestMoneyFilter,
                                                         @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc);


    @PostMapping("/v1/pg/refund")
    ApiResponse doRefund(@RequestBody RefundRequest refundRequest);

    @PostMapping("/v1/pg/contacts/create")
    ApiResponse createContact(@RequestBody ContactRequest contactRequest) throws Exception;

    @GetMapping("/v1/wallet/transaction-counts/{type}")
    ResponseEntity<ApiResponse> getWalletTransactionCountByType(@PathVariable(name = "type") PurchaseType type);

    @RequestMapping(value = "/v1/wallet/addReward", method = RequestMethod.POST)
    ResponseEntity<ApiResponse> addRewardToWallet(@RequestBody WalletRequest walletRequest);

    @PostMapping(value = "/v1/wallet/list/report")
    public ResponseEntity<ApiResponse> getAllWalletTransactionForReport(@RequestBody WalletFilterRequest walletFilterRequest);

    }
