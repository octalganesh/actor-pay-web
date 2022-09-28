package com.octal.actorPay.client;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;


@FeignClient(name = EndPointConstants.USER_MICROSERVICE,
        url = "http://localhost:8084/",
        configuration = FeignSupportConfig.class)
public interface UserServiceClient {

    @PostMapping("/productCommission/list/paged")
    ResponseEntity<ApiResponse> getProductAllCommissions(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                                @RequestBody ProductCommissionFilterRequest filterRequest);



    @GetMapping(value = "/users/get/user/identity/{userIdentity}")
    public ResponseEntity<ApiResponse> getCustomerIdentity(@PathVariable("userIdentity") String userIdentity);

    @PostMapping(value = "/v1/bank/transfer")
    public ResponseEntity<ApiResponse> transferToAnotherBank(@Valid @RequestBody RazorpayPayoutRequest payoutRequest);

    @PostMapping(value = "/v1/bank/transfer/checkout")
    public ResponseEntity<ApiResponse> transferCheckOut(@Valid @RequestBody BankTransferRequest bankTransferRequest);

    @PostMapping(value = "/v1/bank/list/transactions")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestParam(name = "userType", required = false, defaultValue = "merchant") String userType,
                                                             @RequestBody BankTransactionFilterRequest filterRequest);

    @PostMapping(value = "/v1/bank/list/transactions/report")
    public ResponseEntity<ApiResponse> getAllTransactionForReport(@RequestBody BankTransactionFilterRequest filterRequest);
}
