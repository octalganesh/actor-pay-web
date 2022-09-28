package com.octal.actorPay.controller;

import com.google.gson.Gson;
import com.octal.actorPay.client.AdminPaymentClient;
import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.dto.AdminDashboardDTO;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.AdminDashboardRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.AdminService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AdminDashboardController {


    private AdminService adminService;
    private AdminUserService adminUserService;
    private MerchantClient merchantClient;
    private AdminPaymentClient adminPaymentClient;

    public AdminDashboardController(AdminService adminService, AdminUserService adminUserService, MerchantClient merchantClient, AdminPaymentClient adminPaymentClient) {
        this.adminService = adminService;
        this.adminUserService = adminUserService;
        this.merchantClient = merchantClient;
        this.adminPaymentClient = adminPaymentClient;
    }

    @Secured("ROLE_DASHBOARD_TOTAL_COUNTS")
    @PostMapping("/dashboard/getTotalCounts")
    public ResponseEntity<ApiResponse> getTotalCounts(HttpServletRequest request, @RequestBody AdminDashboardRequest adminDashboardRequest) {
        String userName = request.getHeader("userName");
        try {
            User user = adminService.getUserByEmailId(userName);
            AdminDashboardDTO response = new AdminDashboardDTO();
            Gson gson = new Gson();
            ResponseEntity<ApiResponse> userCountResponse = adminUserService.getAllUsersCount();
            if(userCountResponse.getBody() != null && userCountResponse.getBody().getData() != null){
                String usersCountResponseJson = gson.toJson(userCountResponse.getBody().getData());
                AdminDashboardDTO usersCountResponse = gson.fromJson(usersCountResponseJson,AdminDashboardDTO.class);
                response.setTotalUsers(usersCountResponse.getTotalUsers());
                response.setTotalActiveUsers(usersCountResponse.getTotalActiveUsers());
                response.setTotalInActiveUsers(usersCountResponse.getTotalInActiveUsers());
            }
            ResponseEntity<ApiResponse> merchantCountResponse = merchantClient.getAllMerchantsCount();
            if(merchantCountResponse.getBody() != null && merchantCountResponse.getBody().getData() != null){
                if(merchantCountResponse.getBody().getData().toString() != null && !merchantCountResponse.getBody().getData().toString().equalsIgnoreCase("")){
                    response.setTotalVendors(Long.valueOf(merchantCountResponse.getBody().getData().toString()));
                }
            }
            ResponseEntity<ApiResponse> shoppingTransactionCountResponse = adminPaymentClient.getWalletTransactionCountByType(PurchaseType.SHOPPING);
            if(shoppingTransactionCountResponse.getBody()  != null && shoppingTransactionCountResponse.getBody().getData() != null){
                if(shoppingTransactionCountResponse.getBody().getData().toString() != null && !shoppingTransactionCountResponse.getBody().getData().toString().equalsIgnoreCase("")){
                    response.setTotalTransaction(Long.valueOf(shoppingTransactionCountResponse.getBody().getData().toString()));
                }
            }
            ResponseEntity<ApiResponse> walletTransactionCountResponse = adminPaymentClient.getWalletTransactionCountByType(PurchaseType.ADDED_MONEY_TO_WALLET);
            if(walletTransactionCountResponse.getBody()  != null && walletTransactionCountResponse.getBody().getData() != null){
                if(walletTransactionCountResponse.getBody().getData().toString() != null && !walletTransactionCountResponse.getBody().getData().toString().equalsIgnoreCase("")){
                    response.setTotalTransactionUnderCategory(Long.valueOf(walletTransactionCountResponse.getBody().getData().toString()));
                }
            }
            ResponseEntity<ApiResponse> allProductsResponse = merchantClient.getAllProductProductCount();
            if(allProductsResponse.getBody()  != null && allProductsResponse.getBody().getData() != null){
                if(allProductsResponse.getBody().getData().toString() != null && !allProductsResponse.getBody().getData().toString().equalsIgnoreCase("")){
                    response.setTotalProducts(Long.valueOf(allProductsResponse.getBody().getData().toString()));
                }
            }
            ResponseEntity<ApiResponse> totalEarning = adminUserService.getAdminTotalCommissionByOrderStatus("SUCCESS");
            if(totalEarning.getBody()  != null && totalEarning.getBody().getData() != null){
                if(totalEarning.getBody().getData().toString() != null && !totalEarning.getBody().getData().toString().equalsIgnoreCase("")){
                    response.setTotalEarning(Float.valueOf(totalEarning.getBody().getData().toString()));
                }
            }
            return new ResponseEntity<>(new ApiResponse("Dashboard Details.", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error " + e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
