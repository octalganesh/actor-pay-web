package com.octal.actorPay.controller;

import com.octal.actorPay.client.UserServiceClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.MerchantService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MerchantCommissionController extends BaseController {

    private UserServiceClient userServiceClient;

    private MerchantService merchantService;

    public MerchantCommissionController(UserServiceClient userServiceClient, MerchantService merchantService) {
        this.userServiceClient = userServiceClient;
        this.merchantService = merchantService;
    }

    @Secured("ROLE_COMMISSION_LIST_VIEW")
    @PostMapping("/productCommission/list/paged")
    public ResponseEntity<ApiResponse> getProductAllCommissions(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                                @RequestBody ProductCommissionFilterRequest filterRequest,
                                                                HttpServletRequest request) throws Exception {
        try {
            User user = getAuthorizedUser(request);
            MerchantDetails merchantDetails = merchantService.getUserByEmailId(user.getEmail()).getMerchantDetails();
            filterRequest.setMerchantName(merchantDetails.getBusinessName());
            filterRequest.setMerchantId(merchantDetails.getId());
            System.out.println("========= == ======  "+merchantDetails.getBusinessName());
            ResponseEntity<ApiResponse> responseEntity = userServiceClient.getProductAllCommissions(pageNo, pageSize, sortBy, asc, filterRequest);
            return responseEntity;
        } catch (FeignException feignException) {
            ExceptionUtils.parseFeignExceptionMessage(feignException);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: Contact Administrator " + e.getMessage(),
                    null, HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
