package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.PayrollWalletHistoryDTO;
import com.octal.actorPay.dto.WalletTransactionResponse;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.dto.request.CommonUserResponse;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.listener.events.AddMoneyIntoWallet;
import com.octal.actorPay.service.WalletService;
import com.octal.actorPay.utils.PaymentConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/v1/wallet")
public class WalletController extends PagedItemsController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse> saveWallet(@Valid @RequestBody WalletDTO walletDTO, HttpServletRequest request) throws Exception {
        WalletDTO walletObject = walletService.getWalletBalanceByUserId(walletDTO.getUserId(), walletDTO.getUserType());
        if (walletObject == null)
            walletObject = walletService.createUserWallet(walletDTO);
        return new ResponseEntity<>(new ApiResponse("Created user wallet", walletObject, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/addMoney")
    public ResponseEntity<ApiResponse> addMoneyToWallet(@Valid @RequestBody WalletRequest request) throws Exception {
        WalletTransactionResponse walletTransactionResponse = null;
        if (request.getUserType().equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
            walletTransactionResponse = walletService.addMoneyToAdminWallet(request);
        } else {
            walletTransactionResponse = walletService.addMoneyToWallet(request);
        }
        if(walletTransactionResponse != null){

//            try {
//                eventPublisher.publishEvent(new AddMoneyIntoWallet(new User(),walletTransactionResponse));
//            }catch (Exception e){
//                System.out.println(e);
//            }
            return new ResponseEntity<>(new ApiResponse("Transaction Details: ", walletTransactionResponse
                    , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ApiResponse("Something went wrong", null
                , String.valueOf(500), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/addReward")
    public ResponseEntity<ApiResponse> addRewardToWallet(@Valid @RequestBody WalletRequest request) throws Exception {
        WalletTransactionResponse
            walletTransactionResponse = walletService.addRewardToWallet(request);

        return new ResponseEntity<>(new ApiResponse("Transaction Details: ", walletTransactionResponse
                , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transfer(@RequestBody WalletRequest walletRequest) throws Exception {
        WalletTransactionResponse walletTransactionResponse = walletService.transferMoney(walletRequest);
//        if(walletTransactionResponse != null){
//            walletTransactionResponse.setModeOfTransaction("WalletToWallet");
//            eventPublisher.publishEvent(new AddMoneyIntoWallet(new User(),walletTransactionResponse));
//        }
        return new ResponseEntity<>(new ApiResponse("Transaction Details: "
                , walletTransactionResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@RequestBody WalletRequest withdrawRequest) throws Exception {
        WalletTransactionResponse walletTransactionResponse = walletService.withDrawMoney(withdrawRequest);
        return new ResponseEntity<>(new ApiResponse("Transaction Details: ", walletTransactionResponse,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTxn(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                       @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                       @RequestParam(name = "userType", required = false) String userType,
                                                       @RequestBody WalletFilterRequest walletFilterRequest) throws Exception {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<WalletTransactionDTO> walletTransactionDTOList =
                walletService.searchWalletTransactions(walletFilterRequest, pagedInfo);
        ApiResponse apiResponse = new ApiResponse("Wallet Search Result: ", walletTransactionDTOList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);

       /* Object data = apiResponse.getData();
        JSONObject jsonObject = new JSONObject((Map<String, ?>) data);
        Number noOfTrasaction = jsonObject.getAsNumber("totalItems");
        System.out.println("Total transaction: "+noOfTrasaction);*/

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/list/report")
    public ResponseEntity<ApiResponse> getAllWalletTransactionForReport(@RequestBody WalletFilterRequest walletFilterRequest) throws Exception {

        List<WalletTransactionDTO> walletTransactionDTOList =
                walletService.getAllWalletTransactionForReport(walletFilterRequest);
        ApiResponse apiResponse = new ApiResponse("Wallet Search Result: ", walletTransactionDTOList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse> getWalletBalance(@RequestParam(value = "userName", required = false) String userName, @RequestParam("userType") String userType, HttpServletRequest request) throws Exception {
        try {
            if (StringUtils.isBlank(userName)) {
                userName = request.getHeader("userName");
            }
            WalletDTO walletDTO = walletService.getWalletBalanceByUserId(userName, userType);
            return new ResponseEntity<>(new ApiResponse("Wallet Balance: ",
                    walletDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(),
                    null, String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping("/admin/balance/{userId}")
    public ResponseEntity<ApiResponse> getWalletBalanceByAdmin(@PathVariable("userId") String userId, HttpServletRequest request) throws Exception {
        WalletDTO walletDTO = walletService.getWalletBalanceByUserId(userId);
        return new ResponseEntity<>(new ApiResponse("Wallet Balance: ",
                walletDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/requestMoney")
    public ResponseEntity<ApiResponse> requestMoney(@RequestBody RequestMoneyDTO requestMoneyDTO, HttpServletRequest request) throws Exception {
        String loggedInUser = request.getHeader("userName");

        requestMoneyDTO = walletService.requestMoney(requestMoneyDTO);
        if (requestMoneyDTO == null) {
            return new ResponseEntity<>(new ApiResponse(String.valueOf(new StringBuffer().append("  You can not request to self")),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Money has been Requested  ",
                    requestMoneyDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/requestMoney/list/paged")
    public ResponseEntity<ApiResponse> getRequestMoney(
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
            @RequestBody RequestMoneyFilter requestMoneyFilter) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<RequestMoneyDTO> requestMoneyDTOS = walletService.getRequestMoney(requestMoneyFilter, pagedInfo);
        return new ResponseEntity<>(new ApiResponse("Request Money Details: ",
                requestMoneyDTOS, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/requestMoney/acceptordecline")
    public ResponseEntity<ApiResponse> acceptOrDeclineRequest(@RequestBody RequestMoneyDTO requestMoneyDTO) throws Exception {
        requestMoneyDTO.setTransferMode(PaymentConstants.TRANSFER_MODE);
        RequestMoneyResponse requestMoneyResponse = walletService.acceptOrDecline(requestMoneyDTO);

        return new ResponseEntity<>(new ApiResponse("Request Money Response ",
                requestMoneyResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/requestMoney/cancel/{requestId}/{toUserId}")
    public ResponseEntity<ApiResponse> cancelMoneyRequest(@PathVariable("requestId") String requestId,
                                                          @PathVariable("toUserId") String toUserId) throws Exception {
        RequestMoneyDTO requestMoneyDTO = walletService.cancelMoneyRequest(requestId, toUserId);
        return new ResponseEntity<>(new ApiResponse("Request Money Cancellation ",
                requestMoneyDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("{userId}/{userType}/validate")
    public ResponseEntity<ApiResponse> checkWalletStatus(@PathVariable("userId") String userId,
                                                         @PathVariable("userType") String userType)
            throws Exception {
        String walletUserId = walletService.getWalletByUserIdAndUserType(userId, userType);
        if (StringUtils.isBlank(walletUserId)) {
            WalletDTO walletDTO = new WalletDTO();
            walletDTO.setUserType(userType);
            walletDTO.setUserId(userId);
            WalletDTO newWallet = walletService.createUserWallet(walletDTO);
            return new ResponseEntity<>(new ApiResponse("Wallet UserId ", newWallet
                    , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Wallet UserId ", null
                    , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping("/users/get/{userIdentity}")
    public ResponseEntity<ApiResponse> findUserType(@PathVariable("userIdentity") String userIdentity) {
    try {
    CommonUserResponse commonUserResponse = walletService.findUserType(userIdentity);
    return new ResponseEntity<>(new ApiResponse("User Details ", commonUserResponse
            , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }catch (Exception e){
        return new ResponseEntity<>(new ApiResponse("User not found ", null
                , String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
    }
    }
    @GetMapping("/transaction-counts/{type}")
    public ResponseEntity<ApiResponse> transactionCountsByType(@PathVariable("type") PurchaseType type) {
        return new ResponseEntity<>(new ApiResponse("Transaction Counts ", walletService.getTransactionCountByType(type)
                , String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/payroll/csv")
    public ResponseEntity<ApiResponse> readCSVFile(@RequestParam(name = "userId") String userId,
                                   @RequestParam(name = "userType", required = false) String userType,
                                   @RequestBody ByteArrayResource requestFile) {

        return new ResponseEntity<>(new ApiResponse("CSV uploaded successfully",
                walletService.readCSVFile(userId, userType, requestFile), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/payroll/ready")
    public ApiResponse getReadyPayrolls(@RequestParam(name = "userId") String userId) throws Exception {

        return new ApiResponse("Payrolls Details",
                walletService.getReadyPayrolls(userId), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/payroll/process")
    public ApiResponse processPayrolls(@RequestBody List<String> payrollIds) throws Exception {
        walletService.processPayrolls(payrollIds);
        return new ApiResponse("Payrolls Details",
                "Payroll proceed successfully", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
    }

    @PostMapping("/payroll/list/paged")
    public ResponseEntity<ApiResponse>  searchPayrollDetails(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                       @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                       @RequestParam(name = "userType", required = false) String userType,
                                                       @RequestBody WalletFilterRequest walletFilterRequest) throws Exception {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<PayrollWalletHistoryDTO> searchPayrollDetails =
                walletService.searchPayrollDetails(walletFilterRequest, pagedInfo);
        ApiResponse apiResponse = new ApiResponse("Payroll History Result: ", searchPayrollDetails, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
