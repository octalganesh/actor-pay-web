package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.exceptions.ExceptionUtils;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/taxes")
public class MerchantTaxController extends BaseController {


    private AdminFeignClient adminFeignClient;

    public MerchantTaxController(AdminFeignClient adminFeignClient) {
        this.adminFeignClient = adminFeignClient;
    }

    @Secured("ROLE_TAX_VIEW_BY_HSN")
    @GetMapping("/hsncode/{hsnCode}")
    public ResponseEntity<ApiResponse> getProductByHsnCode(@PathVariable("hsnCode") String hsnCode) {
        return adminFeignClient.getProductByHsnCode(hsnCode);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getAllActiveTaxes(@RequestParam(required = false) Integer pageNo,
                                                         @RequestParam(required = false) Integer pageSize,
                                                         @RequestParam(defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(defaultValue = "false") boolean asc) {
        if (pageNo == null || pageSize == null) {
            return adminFeignClient.getAllActiveTaxes(pageNo, pageSize, sortBy, asc);
        } else {
            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
            return adminFeignClient.getAllActiveTaxes(pageNo, pageSize, sortBy, asc);
        }


    }

    @Secured("ROLE_TAX_BY_ID")
    @GetMapping("/{taxId}")
    public ResponseEntity<ApiResponse> getTaxById(@PathVariable("taxId") String taxId) {
        return adminFeignClient.getTaxById(taxId);
    }

    @Secured("ROLE_TAX_LIST_VIEW")
    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse> getAllTaxes(@RequestParam(required = false) Integer pageNo,
                                                   @RequestParam(required = false) Integer pageSize,
                                                   @RequestParam(defaultValue = "hsnCode", required = false) String sortBy,
                                                   @RequestParam(defaultValue = "false", required = false) boolean asc,
                                                   @RequestParam(name = "isActive", required = false, defaultValue = "true")
                                                           Boolean isActive) throws Exception {

        try {
            return adminFeignClient.getAllTaxes(pageNo, pageSize, sortBy, asc, isActive);
        } catch (
                FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
