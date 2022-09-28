package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.AdminBankService;
import com.octal.actorPay.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/bank")
public class AdminBankController {

    private AdminBankService adminBankService;

    private AdminService adminService;


    public AdminBankController(AdminBankService adminBankService, AdminService adminService) {
        this.adminBankService = adminBankService;
        this.adminService = adminService;
    }

    @Secured("ROLE_BANK_TRANSACTIONS_LIST_VIEW")
    @PostMapping(value = "/list/transactions")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestBody BankTransactionFilterRequest filterRequest,
                                                             HttpServletRequest request) throws Exception {
        filterRequest.setUserType(CommonConstant.USER_TYPE_ADMIN);
        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        if (user == null || !user.getActive().booleanValue()) {
            throw new RuntimeException("User is not Active to Request Transaction");
        }
        filterRequest.setAdminId(user.getId());
        return adminBankService.searchBankTransaction(pageNo, pageSize, sortBy, asc, filterRequest);
    }
}
