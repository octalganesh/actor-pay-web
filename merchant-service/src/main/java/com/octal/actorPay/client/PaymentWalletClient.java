package com.octal.actorPay.client;

import com.octal.actorPay.config.FeignConfig;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankAccountRequest;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.DeactivateRequest;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.UpdateFundAccountRequest;
import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "payment-service", url = "http://localhost:8096/", configuration = {FeignConfig.class})
@Service
public interface PaymentWalletClient {

    @PostMapping("/v1/wallet/create")
    ResponseEntity<ApiResponse> createWallet(@RequestBody @Valid WalletDTO walletDTO);

    @RequestMapping(value = "/v1/wallet/addMoney", method = RequestMethod.POST)
    ResponseEntity<ApiResponse> addMoneyToWallet(@RequestBody WalletRequest walletRequest);

    @PostMapping(value = "/v1/wallet/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                               @RequestParam(name = "userType", required = false) String userType,
                                                               @RequestBody WalletFilterRequest walletFilterRequest);

    @PostMapping(value = "/v1/wallet/list/report")
    public ResponseEntity<ApiResponse> getAllWalletTransactionForReport(@RequestBody WalletFilterRequest walletFilterRequest);

    @PostMapping(value = "/v1/wallet/withdraw")
    ApiResponse withdraw(@RequestBody WalletRequest withdrawRequest);

    @PostMapping(value = "/v1/wallet/transfer")
    ApiResponse transferMoney(@RequestBody WalletRequest walletRequest);

    @GetMapping("/v1/wallet/balance")
    ApiResponse getWalletBalance(@RequestParam("userType") String userType);

    @GetMapping("/v1/wallet/{userId}/balance")
    ApiResponse r(@PathVariable(name = "userId") String userId);

    @PostMapping("/v1/wallet/requestMoney")
    ApiResponse requestMoney(@RequestBody RequestMoneyDTO requestMoneyDTO);

    @PostMapping("/v1/wallet/requestMoney/list/paged")
    ApiResponse getRequestMoney(@RequestBody RequestMoneyFilter requestMoneyFilter,
                                @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                @RequestParam(name = "asc", defaultValue = "false") boolean asc);

    @PutMapping("/v1/wallet/requestMoney/acceptordecline")
    ApiResponse acceptOrDeclineRequest(@RequestBody RequestMoneyDTO requestMoneyDTO);

    @GetMapping("{userId}/{userType}/validate")
    ApiResponse checkWalletStatus(@PathVariable(name = "userId") String userId,
                                  @PathVariable(name = "userType") String userType);

    @PutMapping("/v1/wallet/requestMoney/cancel/{requestId}/{toUserId}")
    ApiResponse cancelMoneyRequest(@PathVariable(name = "requestId") String requestId, @PathVariable(name = "toUserId") String toUserId);

    @GetMapping("/v1/wallet/users/get/{userIdentity}")
    ResponseEntity<ApiResponse> findUserType(@PathVariable("userIdentity") String userIdentity);

    @PostMapping("/v1/pg/order/create")
    ApiResponse createOrder(@RequestBody OrderRequest orderRequest);

    @PostMapping("v1/pg/fundaccount/create")
    ApiResponse createFundAccount(@RequestBody BankAccountRequest addBankRequest);

    @PostMapping("v1/pg/contacts/create")
    ApiResponse createContact(@RequestBody ContactRequest contactRequest);

    @GetMapping("/v1/pg/fundaccount/{userId}/{userType}")
    ApiResponse getUserFundAccounts(@PathVariable(name = "userId") String userId);

    @GetMapping("/v1/pg/fundaccount/{userId}/{fundAccountId}")
    ApiResponse getUserFundAccountByFundAccountId(@PathVariable(name = "userId") String userId,
                                                  @PathVariable(name = "fundAccountId") String fundAccountId);

    @PutMapping("/v1/pg/fundaccount/activeordeactive")
    ApiResponse activeOrDeActiveAccount(DeactivateRequest deactivateRequest);

    @PutMapping("/v1/pg/fundaccount/update")
    ApiResponse setPrimaryOrSecondaryAccount(UpdateFundAccountRequest deactivateRequest);

    @PutMapping("/v1/pg/fundaccount/update-self")
    ApiResponse setSelfAccount(UpdateFundAccountRequest deactivateRequest);

    @GetMapping("/v1/pg/fundaccount/self/{userId}/{userType}")
    ApiResponse getUsersSelfFundAccounts(@PathVariable(name = "userId") String userId);

    @GetMapping("/v1/pg/fundaccount/beneficiary/{userId}/{userType}")
    ApiResponse getUsersBeneficiaryAccounts(@PathVariable(name = "userId") String userId);

    @PostMapping(value = "/v1/wallet/payroll/csv")
    public ApiResponse readCSVFile(@RequestParam(name = "userId") String userId,
                                   @RequestParam(name = "userType", required = false) String userType,
                                   @RequestBody ByteArrayResource requestFile);

    @GetMapping("/v1/wallet/payroll/ready")
    public ApiResponse getReadyPayrolls(@RequestParam(name = "userId") String userId) throws Exception;

    @PostMapping("/v1/wallet/payroll/process")
    public ApiResponse processPayrolls(@RequestBody List<String> payrollIds) throws Exception;

    @PostMapping("/v1/wallet/payroll/list/paged")
    public ApiResponse searchPayrollDetails(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestParam(name = "userType", required = false) String userType,
                                                             @RequestBody WalletFilterRequest walletFilterRequest) throws Exception;
}
