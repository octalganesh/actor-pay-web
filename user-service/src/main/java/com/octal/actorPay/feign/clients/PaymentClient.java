package com.octal.actorPay.feign.clients;

import com.octal.actorPay.config.FeignConfig;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient(name = "payment-service", url = "http://localhost:8096/", configuration = {FeignConfig.class})
@Service
public interface PaymentClient {

    @RequestMapping(value = "/v1/wallet/create", method = RequestMethod.POST)
    void createWallet(@RequestBody @Valid WalletRequest walletBalanceRequest);

    @RequestMapping(value = "/v1/wallet/addMoney", method = RequestMethod.POST)
    ResponseEntity<ApiResponse> addMoneyToWallet(@RequestBody WalletRequest walletRequest);

    @RequestMapping(value = "/v1/wallet/addReward", method = RequestMethod.POST)
    ResponseEntity<ApiResponse> addRewardToWallet(@RequestBody WalletRequest walletRequest);

    @PostMapping(value = "/v1/wallet/list/paged")
    ResponseEntity<ApiResponse> searchWalletTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                        @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                        @RequestParam(name = "userType", required = false) String userType,
                                                        @RequestBody WalletFilterRequest walletFilterRequest);

    @PostMapping(value = "/v1/wallet/withdraw")
    ResponseEntity<ApiResponse> withdraw(@RequestBody WalletRequest withdrawRequest);

    @PostMapping(value = "/v1/wallet/transfer")
    ApiResponse transferMoney(@RequestBody WalletRequest walletRequest);

    @GetMapping("/v1/wallet/balance")
    ApiResponse getWalletBalance(@RequestParam("userName") String userName, @RequestParam("userType") String userType);

//    @GetMapping("/v1/wallet/balance")
//    ApiResponse getWalletBalance(@RequestParam("userType") String userType);

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

    @PutMapping("/v1/wallet/requestMoney/cancel/{requestId}/{toUserId}")
    ApiResponse cancelMoneyRequest(@PathVariable(name = "requestId") String requestId, @PathVariable(name = "toUserId") String toUserId);

    @GetMapping("{userId}/{userType}/validate")
    ApiResponse checkWalletStatus(@PathVariable(name = "userId") String userId,
                                  @PathVariable(name = "userType") String userType);

    @GetMapping("/v1/wallet/users/get/{userIdentity}")
    ResponseEntity<ApiResponse> findUserType(@PathVariable("userIdentity") String userIdentity);

    @PostMapping("/v1/pg/order/create")
    ApiResponse createOrder(@RequestBody OrderRequest orderRequest);

    @PostMapping("/v1/pg/order/verify")
    ApiResponse verify(@RequestBody PaymentGatewayResponse paymentGatewayResponse);

    @PostMapping("/v1/pg/order/pgdetails")
    ApiResponse savePgDetails(@RequestBody PgDetailsDTO pgDetailsDTO);

    @PostMapping("/v1/pg/qrcode/create")
    ApiResponse createQrCode(@RequestBody QrCodeCreateRequest request);

    @PostMapping("/v1/pg/refund")
    ApiResponse doRefund(@RequestBody RefundRequest refundRequest);

    @GetMapping("/v1/pg/payment/{paymentTypeId}")
    ApiResponse getPGPaymentDetails(@PathVariable(name = "paymentTypeId") String paymentTypeId);

    @PostMapping("v1/pg/fundaccount/create")
    ApiResponse createFundAccount(@RequestBody BankAccountRequest addBankRequest);

    @PostMapping("v1/pg/contacts/create")
    ApiResponse createContact(@RequestBody ContactRequest contactRequest);

    @GetMapping("/v1/pg/fundaccount/{userId}/{userType}")
    ApiResponse getUserFundAccounts(@PathVariable(name = "userId") String userId);

    @GetMapping("/v1/pg/fundaccount/self/{userId}/{userType}")
    ApiResponse getUsersSelfFundAccounts(@PathVariable(name = "userId") String userId);

    @GetMapping("/v1/pg/fundaccount/beneficiary/{userId}/{userType}")
    ApiResponse getUsersBeneficiaryAccounts(@PathVariable(name = "userId") String userId);

    @GetMapping("/v1/pg/fundaccount/{userId}/{fundAccountId}")
    ApiResponse getUserFundAccountByFundAccountId(@PathVariable(name = "userId") String userId,
                                                   @PathVariable(name = "fundAccountId") String fundAccountId);

    @PutMapping("/v1/pg/fundaccount/activeordeactive")
    ApiResponse activeOrDeActiveAccount(DeactivateRequest deactivateRequest);

    @PostMapping("/v1/pg/payout/create")
    ApiResponse createPayout(PayoutRequest payoutRequest);

    @PutMapping("/v1/pg/fundaccount/update")
    ApiResponse setPrimaryOrSecondaryAccount(UpdateFundAccountRequest deactivateRequest);

    @PutMapping("/v1/pg/fundaccount/update-self")
    ApiResponse setSelfAccount(UpdateFundAccountRequest deactivateRequest);

    @PostMapping("/v1/webhook/qr/credit")
    void qrCodePaymentCredit(QrCodeCreditRequest creditRequest);
}
