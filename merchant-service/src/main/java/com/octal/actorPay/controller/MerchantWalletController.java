package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.RequestMoneyDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.MerchantWalletService;
import com.octal.actorPay.utils.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/wallet")
public class MerchantWalletController extends BaseController{

    @Autowired
    private CommonService commonService;

    @Autowired
    private MerchantWalletService merchantWalletService;

    @Autowired
    private MerchantService merchantService;

    public MerchantWalletController(MerchantWalletService merchantWalletService,
                                    MerchantService merchantService, CommonService commonService) {
        this.merchantWalletService = merchantWalletService;
        this.merchantService = merchantService;
        this.commonService = commonService;
    }

    @Secured("ROLE_MERCHANT_ADD_MONEY_TO_WALLET")
    @PostMapping(value = "/addMoney")
    public ResponseEntity<ApiResponse> addMoneyToWallet(@Valid @RequestBody WalletRequest walletRequest,
                                                        HttpServletRequest request) throws Exception {
//        User user = commonService.getLoggedInUser(request);
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Add Money to Wallet");
        }
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(user.getUserType());

        ApiResponse apiResponse = merchantWalletService.addMoneyToWallet(walletRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_MERCHANT_VIEW_BALANCE")
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse> getWalletBalance(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
//        if (!user.getId().equalsIgnoreCase(userId)) {
//            throw new RuntimeException(String.format("Invalid User Id %s ", userId));
//        }
        ApiResponse apiResponse = merchantWalletService.getWalletBalance(CommonConstant.USER_TYPE_MERCHANT);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_TRANSFER_MONEY")
    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transferMoney(@RequestBody @Valid WalletRequest walletRequest,
                                                     HttpServletRequest request) throws Exception {

        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
        if(StringUtils.isBlank(walletRequest.getBeneficiaryUserType())) {
            throw new RuntimeException("beneficiaryUserType: must not be empty");
        }
        walletRequest.setCurrentUserId(user.getId());
        walletRequest.setUserType(user.getUserType());
        walletRequest.setPurchaseType(PurchaseType.TRANSFER);
        walletRequest.setBeneficiaryUserType(walletRequest.getBeneficiaryUserType());
        ApiResponse apiResponse = merchantWalletService.transferMoney(walletRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_LIST_TRANSACTION")
    @PostMapping(value = "/list/paged")
    public ResponseEntity<ApiResponse> searchWalletTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                               @RequestParam(name = "userType", required = false, defaultValue = "merchant") String userType,
                                                               @RequestBody WalletFilterRequest walletFilterRequest,
                                                               HttpServletRequest request) throws Exception {
//        String userName = request.getHeader("userName");
        User user = getAuthorizedUser(request);
        if (userType != null && userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
//            User user = merchantService.getUserByEmailId(userName);
            walletFilterRequest.setUserId(user.getId());
        }
        ApiResponse apiResponse =
                merchantWalletService.searchWalletTransaction(pageNo, pageSize, sortBy, asc, userType, walletFilterRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_REQUEST_MONEY")
    @PostMapping("/requestMoney")
    public ResponseEntity<ApiResponse> requestMoney(@Valid @RequestBody RequestMoneyDTO requestMoneyDTO, HttpServletRequest request) throws Exception {
//        User user = commonService.getLoggedInUser(request);
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Request Money");
        }
        requestMoneyDTO.setUserId(user.getId());
        requestMoneyDTO.setRequestUserTypeFrom(user.getUserType());
        ApiResponse apiResponse = merchantWalletService.requestMoney(requestMoneyDTO);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_LIST_REQUEST_MONEY")
    @PostMapping("/requestMoney/list/paged")
    public ResponseEntity<ApiResponse> getRequestMoney(
            @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "asc", defaultValue = "false") boolean asc,
            @RequestBody RequestMoneyFilter requestMoneyFilter,
            HttpServletRequest request) throws Exception {
//        User user = commonService.getLoggedInUser(request);
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to get Money Request Details");
        }
        requestMoneyFilter.setUserId(user.getId());
        requestMoneyFilter.setToUserId(user.getId());
        requestMoneyFilter.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse = merchantWalletService.getRequestMoney(requestMoneyFilter, pageNo, pageSize, sortBy, asc);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_REQUEST_MONEY_ACCEPT")
    @PutMapping("/requestMoney/{isAccepted}/{requestId}/accept")
    ResponseEntity<ApiResponse> acceptOrDeclineRequest(
            @PathVariable(value = "isAccepted") Boolean isAccepted,
            @PathVariable("requestId") String requestId, HttpServletRequest request) throws Exception {

//        User user = commonService.getLoggedInUser(request);
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to accept or decline Request Money");
        }
        RequestMoneyDTO requestMoneyDTO = new RequestMoneyDTO();
        requestMoneyDTO.setRequestId(requestId);
        requestMoneyDTO.setToUserId(user.getId());
        requestMoneyDTO.setAccepted(isAccepted);
        ApiResponse apiResponse = merchantWalletService.acceptOrDeclineRequest(requestMoneyDTO);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_MERCHANT_REQUEST_MONEY_CANCEL")
    @PutMapping("/requestMoney/cancel/{requestId}")
    ResponseEntity<ApiResponse> cancelMoneyRequest(@PathVariable(name = "requestId") String requestId, HttpServletRequest request) {
//        User user = commonService.getLoggedInUser(request);
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Cancel Money Request");
        }
        ApiResponse apiResponse = merchantWalletService.cancelMoneyRequest(requestId,user.getId());
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
        withdrawRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse = merchantWalletService.withdraw(withdrawRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_WALLET_PAYROLL_READ_CSV")
    @PostMapping(value = "/payroll/csv")
    public ResponseEntity<ApiResponse> readCSVFile(@RequestBody MultipartFile requestFile,
                                                HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }

        ApiResponse apiResponse = merchantWalletService.readCSVFile(requestFile, user.getId());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_WALLET_PAYROLL_READY_VIEW")
    @PostMapping(value = "/payroll/ready")
    public ResponseEntity<ApiResponse> getReadyPayrolls(HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }

        ApiResponse apiResponse = merchantWalletService.getReadyPayrolls(user.getId());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Secured("ROLE_WALLET_PAYROLL_PROCESS")
    @PostMapping("/payroll/process")
    public ResponseEntity<ApiResponse> processPayrolls(@RequestBody List<String> payrollIds, HttpServletRequest request) throws Exception {
        User user = commonService.getLoggedInUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to Withdraw Money from Wallet");
        }
        ApiResponse apiResponse = merchantWalletService.processPayrolls(payrollIds);
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


        User user = getAuthorizedUser(request);
        walletFilterRequest.setUserId(user.getId());
        walletFilterRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse =
                merchantWalletService.searchPayrollDetails(pageNo, pageSize, sortBy, asc, userType, walletFilterRequest);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
