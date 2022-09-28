package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ProductCommissionDTO;
import com.octal.actorPay.dto.ProductCommissionReport;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.service.ProductCommissionService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductCommissionController extends PagedItemsController {

    private ProductCommissionService productCommissionService;
    private MerchantClient merchantClient;

    public ProductCommissionController(ProductCommissionService productCommissionService, MerchantClient merchantClient) {
        this.productCommissionService = productCommissionService;
        this.merchantClient = merchantClient;
    }

    @PostMapping("/productCommission/list/paged")
    public ResponseEntity<ApiResponse> getProductAllCommissions(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                                @RequestBody ProductCommissionFilterRequest filterRequest) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        ProductCommissionReport<ProductCommissionDTO> pageResult = productCommissionService.getAllProductCommission(pagedInfo, filterRequest);
        return new ResponseEntity<>(new ApiResponse("Product Commission list", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/productCommission/update")
    public ResponseEntity<ApiResponse> updateProductCommission(@RequestParam("status") String settlementStatus,
                                                               @RequestParam("ids") List<String> ids) throws Exception {
        productCommissionService.updateProductCommissionStatus(settlementStatus, ids);
        return new ResponseEntity<>(new ApiResponse("Product Commission Status updated Successfully", "", HttpStatus.OK.name(),
                HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/merchantBusinessName/{businessName}")
    public ResponseEntity<ApiResponse> findByMerchantBusinessName(@PathVariable("businessName") String businessName) throws Exception {

        try {
            ResponseEntity<ApiResponse> responseEntity = merchantClient.findByMerchantBusinessName(businessName);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = responseEntity.getBody();
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Merchant not found", "", HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException feignException) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(feignException);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/byName/{productName}")
    public ResponseEntity<ApiResponse> findByName(@PathVariable("productName") String productName) throws Exception {

        try {
            ResponseEntity<ApiResponse> responseEntity = merchantClient.findByName(productName);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = responseEntity.getBody();
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Product not found", "", HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException feignException) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(feignException);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/admin-total-commission/{orderStatus}")
    public ResponseEntity<ApiResponse> getAdminTotalCommissionByOrderStatus(@PathVariable("orderStatus") String orderStatus) throws Exception {
        return new ResponseEntity<>(new ApiResponse("Total Admin Commission.", productCommissionService.getAdminTotalCommissionByOrderStatus(orderStatus), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}
