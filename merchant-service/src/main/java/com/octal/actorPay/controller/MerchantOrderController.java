package com.octal.actorPay.controller;

import com.octal.actorPay.client.MerchantOrderClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.MerchantService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class MerchantOrderController extends BaseController {

    private MerchantOrderClient merchantOrderClient;

    private MerchantService merchantService;

    public MerchantOrderController(MerchantService merchantService, MerchantOrderClient merchantOrderClient) {
        this.merchantOrderClient = merchantOrderClient;
        this.merchantService = merchantService;
    }

    @Secured("ROLE_ORDER_LIST_VIEW")
    @PostMapping("/list/paged")
    ResponseEntity<ApiResponse> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "false") boolean asc,
                                             @RequestBody OrderFilterRequest filterRequest, HttpServletRequest request) {
//        String userName = request.getHeader("userName");
//        MerchantDetails merchantDetails = merchantService.getMerchantIdByEmail(userName);
        User user = getAuthorizedUser(request);
        filterRequest.setMerchantId(user.getMerchantDetails().getId());
        filterRequest.setUserId(user.getId());
        ResponseEntity<ApiResponse> apiResponseResponseEntity
                = merchantOrderClient.viewAllOrder(pageNo, pageSize, sortBy, asc, filterRequest);
        return apiResponseResponseEntity;
    }

    @Secured("ROLE_ORDER_CANCEL_BY_ORDERNO")
    @PostMapping("/cancel/{orderNo}")
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable(name = "orderNo") String orderNo,
                                            @Valid @RequestPart(name = "cancelOrder") String cancelOrder,
                                            @RequestPart(name = "file", required = false) MultipartFile file, HttpServletRequest request)
            throws Exception {
        try {
            String userName = request.getHeader("userName");
            User user = findByPrimaryMerchantBySubMerchantId(userName);
            MerchantDetails merchantDetails = merchantService.getMerchantIdByEmail(user.getEmail());
            return merchantOrderClient.cancelOrder(orderNo, cancelOrder, file, CommonConstant.USER_TYPE_MERCHANT, merchantDetails.getId());
        } catch (
                FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping(value = "/cancel/test", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
//    ResponseEntity<ApiResponse> testUpload(@RequestPart(name = "file") MultipartFile file,
//                                           @RequestPart(name = "cancelOrder") String  cancelOrder) throws Exception {
//        try {
//            return merchantOrderClient.testUpload(file,cancelOrder);
//        } catch (
//                FeignException feignException) {
//            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
//                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
//        }
//    }

    @Secured("ROLE_ORDER_UPDATE_STATUS")
    @PutMapping("/status")
    ResponseEntity<ApiResponse> updateOrderStatus(@RequestParam("status") String status,
                                                  @RequestParam("orderNo") String orderNo, HttpServletRequest request,
                                                  @RequestBody(required = false) OrderNoteDTO orderNote)
            throws Exception {
        String userName = request.getHeader("userName");
        try {
            User user = findByPrimaryMerchantBySubMerchantId(userName);
            MerchantDetails merchantDetails = merchantService.getMerchantIdByEmail(user.getEmail());
            if (orderNote == null) {
                orderNote = new OrderNoteDTO();
                List<String> orderItemIds = new ArrayList<>();
                orderNote.setOrderItemIds(orderItemIds);
            }
            return merchantOrderClient.updateOrderStatus(status, orderNo,
                    CommonConstant.USER_TYPE_MERCHANT, merchantDetails.getId(), orderNote);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_ORDER_VIEW")
    @GetMapping("/{orderNo}")
    ResponseEntity<ApiResponse> viewOrder(@PathVariable("orderNo") String orderNo, HttpServletRequest request) throws Exception {
        String userName = request.getHeader("userName");
        try {
//            User user = merchantService.getUserByEmailId(userName);
            User user = findByPrimaryMerchantBySubMerchantId(userName);
            String merchantId = user.getMerchantDetails().getId();
            return merchantOrderClient.viewOrder(orderNo, CommonConstant.USER_TYPE_MERCHANT, merchantId);
        } catch (
                FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkout")
    ResponseEntity<ApiResponse> checkout(@RequestBody OrderRequest orderRequest) {
        ApiResponse apiResponse = merchantService.checkout(orderRequest);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }
}
