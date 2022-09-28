package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.UserService;
import com.octal.actorPay.service.UserWalletService;
import com.octal.actorPay.utils.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/wallet")
public class UserWalletController {


    private final String string = "/requestMoney/cancel/{requestId}";
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private UserService userService;

    public UserWalletController(UserWalletService userWalletService,
                                CommonService commonService, UserService userService) {
        this.userWalletService = userWalletService;
        this.commonService = commonService;
        this.userService = userService;
    }

    @PostMapping(value = "/addMoney")
    public ResponseEntity<ApiResponse> addMoneyToWallet(@Valid @RequestBody WalletRequest walletRequest,
                                                        HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Add Money to Wallet");
        }
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userWalletService.addMoneyToWallet(walletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transferMoney(@RequestBody @Valid WalletRequest walletRequest,
                                                     HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
        if (StringUtils.isBlank(walletRequest.getBeneficiaryUserType())) {
            throw new RuntimeException("beneficiaryUserType: must not be empty");
        }
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(user.getUserType());
        walletRequest.setPurchaseType(PurchaseType.TRANSFER);
        walletRequest.setBeneficiaryUserType(walletRequest.getBeneficiaryUserType());
        ApiResponse apiResponse = userWalletService.transferMoney(walletRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping(value = "/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                               @RequestParam(name = "userType", required = false, defaultValue = "customer") String userType,
                                                               @RequestBody WalletFilterRequest walletFilterRequest,
                                                               HttpServletRequest request) throws Exception {
        String userName = request.getHeader("userName");
        if (userType != null && userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
            User user = userService.getUserByEmailId(userName);
            walletFilterRequest.setUserId(user.getId());
        }
        ApiResponse apiResponse =
                userWalletService.searchWalletTransaction(pageNo, pageSize, sortBy, asc, userType, walletFilterRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@RequestBody WalletRequest withdrawRequest,
                                                HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
        withdrawRequest.setCurrentUserId(user.getId());
        withdrawRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userWalletService.withdraw(withdrawRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse> getWalletBalance(HttpServletRequest request) {

        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Request Money");
        }
        ApiResponse apiResponse = userWalletService.getWalletBalance(user.getEmail(), CommonConstant.USER_TYPE_CUSTOMER);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping("/requestMoney")
    public ResponseEntity<ApiResponse> requestMoney(@Valid @RequestBody RequestMoneyDTO requestMoneyDTO, HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Request Money");
        }
        requestMoneyDTO.setUserId(user.getId());
        requestMoneyDTO.setRequestUserTypeFrom(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userWalletService.requestMoney(requestMoneyDTO);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping("/requestMoney/list/paged")
    public ResponseEntity<ApiResponse> getRequestMoney(
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
            @RequestBody RequestMoneyFilter requestMoneyFilter,
            HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to get Money Request Details");
        }
        requestMoneyFilter.setUserId(user.getId());
        requestMoneyFilter.setToUserId(user.getId());
        requestMoneyFilter.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userWalletService.getRequestMoney(requestMoneyFilter, pageNo, pageSize, sortBy, asc);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    @PutMapping("/requestMoney/{isAccepted}/{requestId}/accept")
    ResponseEntity<ApiResponse> acceptOrDeclineRequest(
            @PathVariable(value = "isAccepted") Boolean isAccepted,
            @PathVariable("requestId") String requestId, HttpServletRequest request) throws Exception {
    try{
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to accept or decline Request Money");
        }
        RequestMoneyDTO requestMoneyDTO = new RequestMoneyDTO();
        requestMoneyDTO.setRequestId(requestId);
        requestMoneyDTO.setAccepted(isAccepted);
        requestMoneyDTO.setToUserId(user.getId());
        requestMoneyDTO.setReason("Request Money");
        ApiResponse apiResponse = userWalletService.acceptOrDeclineRequest(requestMoneyDTO);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    } catch (Exception e) {
        return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
    }
    }

    @PutMapping("/requestMoney/cancel/{requestId}")
    ResponseEntity<ApiResponse> cancelMoneyRequest(@PathVariable(name = "requestId") String requestId, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Cancel Money Request");
        }
        ApiResponse apiResponse = userWalletService.cancelMoneyRequest(requestId, user.getId());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
