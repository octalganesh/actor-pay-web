package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.service.AdminPaymentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/wallet")
public class AdminWalletController {


    private AdminPaymentService adminWalletService;

    private AdminService adminService;


    public AdminWalletController(AdminPaymentService adminWalletService, AdminService adminService) {
        this.adminWalletService = adminWalletService;
        this.adminService = adminService;
    }

    @Secured("ROLE_ADD_MONEY_TO_WALLET")
    @PostMapping(value = "/addMoney")
    public ResponseEntity<ApiResponse> addMoneyToWallet(@Valid @RequestBody WalletRequest walletRequest,
                                                        HttpServletRequest request) throws Exception {
        String adminEmail = request.getHeader("userName");
        User user = adminService.getUserByEmailId(adminEmail);
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(CommonConstant.USER_TYPE_ADMIN);
        ApiResponse apiResponse = adminWalletService.addMoneyToWallet(walletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_TRANSFER_MONEY")
    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transferMoney(@RequestBody @Valid WalletRequest walletRequest,
                                                     HttpServletRequest request) throws Exception {
        String adminEmail = request.getHeader("userName");
        User user = adminService.getUserByEmailId(adminEmail);
        if (StringUtils.isBlank(walletRequest.getBeneficiaryUserType())) {
            throw new RuntimeException("beneficiaryUserType: must not be empty");
        }
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(user.getUserType());
        walletRequest.setPurchaseType(PurchaseType.TRANSFER);
        walletRequest.setBeneficiaryUserType(walletRequest.getBeneficiaryUserType());
        ApiResponse apiResponse = adminWalletService.transferMoney(walletRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping(value = "/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@RequestBody WalletRequest withdrawRequest,
                                                HttpServletRequest request) throws Exception {
        String adminEmail = request.getHeader("userName");
        User user = adminService.getUserByEmailId(adminEmail);
        withdrawRequest.setCurrentUserId(user.getId());
        withdrawRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = adminWalletService.withdraw(withdrawRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_LIST_TRANSACTION")
    @PostMapping(value = "/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTransaction(
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
            @RequestBody WalletFilterRequest walletFilterRequest) {
        ApiResponse apiResponse =
                adminWalletService.searchWalletTransaction(pageNo, pageSize, sortBy, asc,
                        CommonConstant.USER_TYPE_ADMIN, walletFilterRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_VIEW_BALANCE")
    @GetMapping("/{userId}/balance")
    public ResponseEntity<ApiResponse> getWalletBalance(@PathVariable(name = "userId") String userId, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        if (user == null || !user.getActive().booleanValue()) {
            throw new RuntimeException("User is not Active to Request Money");
        }
        ApiResponse apiResponse =
                adminWalletService.getWalletBalance(userId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_LIST_REQUEST_MONEY")
    @PostMapping("/requestMoney/list/paged")
    public ResponseEntity<ApiResponse> getRequestMoney(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                       @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                       @RequestBody RequestMoneyFilter requestMoneyFilter) throws Exception {
        requestMoneyFilter.setUserType(CommonConstant.USER_TYPE_ADMIN);
        ApiResponse apiResponse = adminWalletService.getRequestMoney(requestMoneyFilter, pageNo, pageSize, sortBy, asc);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_WALLET_PAYROLL_LIST_VIEW")
    @PostMapping("/payroll/list/paged")
    public ResponseEntity<ApiResponse> searchPayrollDetails(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                            @RequestParam(name = "userType", required = false) String userType,
                                                            @RequestBody WalletFilterRequest walletFilterRequest, HttpServletRequest request) throws Exception {

        walletFilterRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse =
                adminWalletService.searchPayrollDetails(pageNo, pageSize, sortBy, asc, userType, walletFilterRequest);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
