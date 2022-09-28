package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.TaxDTO;
import com.octal.actorPay.dto.request.TaxFilterRequest;
import com.octal.actorPay.service.TaxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/taxes")
public class TaxController extends PagedItemsController {

    private TaxService taxService;


    public TaxController(TaxService taxService) {
        this.taxService = taxService;
    }

    @Secured("ROLE_TAX_ADD")
    @PostMapping
    public ResponseEntity<ApiResponse> addTax(@Valid @RequestBody TaxDTO taxDTO) {
        TaxDTO response = taxService.addTax(taxDTO);
        if (StringUtils.isNotEmpty(response.getId())) {
            return new ResponseEntity<>(new ApiResponse("Tax Data saved successfully", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Issue with saving new Product", null,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_TAX_UPDATE_BY_ID")
    @PutMapping("/{taxId}")
    public ResponseEntity<ApiResponse> updateTax(@Valid @RequestBody TaxDTO taxDTO, @PathVariable("taxId") String taxId) {
        TaxDTO response = taxService.updateTax(taxDTO);
        if (StringUtils.isNotEmpty(response.getId())) {
            return new ResponseEntity<>(new ApiResponse("Tax Data updated successfully", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Issue with saving new Product", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_TAX_CHANGE_STATUS")
    @PutMapping("/status/{taxId}")
    public ResponseEntity<ApiResponse> changeStatus(@RequestParam("active") Boolean active, @PathVariable("taxId") String taxId) {
        TaxDTO response = taxService.changeStatus(taxId, active);
        if (StringUtils.isNotEmpty(response.getId())) {
            return new ResponseEntity<>(new ApiResponse("Tax Data updated successfully", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Issue with saving new Product", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.BAD_REQUEST);
        }
    }


    // This is Filters
    @Secured("ROLE_TAX_LIST_VIEW")
    @GetMapping
    public ResponseEntity<ApiResponse> getAllTaxes(@RequestParam(required = false, defaultValue = "0") Integer pageNo,
                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt", required = false) String sortBy,
                                                   @RequestParam(defaultValue = "false", required = false) boolean asc,
                                                   @RequestParam(name = "hsnCode", required = false) String hsnCode,
                                                   @RequestParam(name = "percentage", required = false) Float percentage,
                                                   @RequestParam(name = "isActive", required = false) Boolean isActive) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        TaxFilterRequest filterRequest = new TaxFilterRequest();
        filterRequest.setPercentage(percentage);
        filterRequest.setStatus(isActive);
        filterRequest.setHsnCode(hsnCode);
        PageItem<TaxDTO> pageResult = taxService.getAllTax(pagedInfo, filterRequest);
        return new ResponseEntity<>(new ApiResponse("Tax list", pageResult,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    // This is for Dropdown
    @Secured("ROLE_TAX_LIST_VIEW")
    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse> getAllTaxes(@RequestParam(required = false) Integer pageNo,
                                                           @RequestParam(required = false) Integer pageSize,
                                                           @RequestParam(defaultValue = "hsnCode", required = false) String sortBy,
                                                           @RequestParam(defaultValue = "false", required = false) boolean asc,
                                                           @RequestParam(name = "isActive", required = false,defaultValue = "true")
                                                               Boolean isActive) {

        return new ResponseEntity<>(new ApiResponse("Tax list",
                taxService.getAllTaxByStatus(isActive, sortBy, asc),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);

    }

//    @GetMapping("/active")
//    public ResponseEntity<ApiResponse> getAllTaxes(@RequestParam(required = false) Integer pageNo,
//                                                   @RequestParam(required = false) Integer pageSize,
//                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
//                                                   @RequestParam(defaultValue = "false") boolean asc) {
//        if (pageNo == null || pageSize == null) {
//            return new ResponseEntity<>(new ApiResponse("Tax list", taxService.getAllActiveTaxes(),
//                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
//        } else {
//            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
//            return new ResponseEntity<>(new ApiResponse("Tax list", taxService.getAllActiveTaxes(pagedInfo),
//                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
//        }
//    }

    @Secured("ROLE_TAX_BY_ID")
    @GetMapping("/{taxId}")
    public ResponseEntity<ApiResponse> getTaxById(@PathVariable("taxId") String taxId) {

        TaxDTO taxDTO = taxService.getTaxById(taxId);
        String message = (taxDTO == null) ? "Tax Data not found" : "";
        return new ResponseEntity<>(new ApiResponse(message, taxDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_TAX_VIEW_BY_HSN")
    @GetMapping("/hsncode/{hsnCode}")
    public ResponseEntity<ApiResponse> getProductByHsnCode(@PathVariable("hsnCode") String hsnCode) {

        TaxDTO taxDTO = taxService.getTaxByHsnCode(hsnCode);
        String message = (taxDTO == null) ? "Tax Data not found" : "";
        return new ResponseEntity<>(new ApiResponse(message, taxDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_TAX_REMOVE_BY_HSN")
    @DeleteMapping("/hsncode/{hsnCode}")
    public ResponseEntity<ApiResponse> removeTaxByHsnCode(@PathVariable("hsnCode") String hsnCode) {
        taxService.removeTaxByHsnCode(hsnCode);
        return new ResponseEntity<>(new ApiResponse("Tax Data Deleted successfully", "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_TAX_REMOVE_BY_ID")
    @DeleteMapping("/byid/{taxId}")
    public ResponseEntity<ApiResponse> removeTaxById(@PathVariable("taxId") String taxId) {
        TaxDTO taxDTO = taxService.removeTax(taxId);
        return new ResponseEntity<>(new ApiResponse(String.format("Tax Data for Tax Id %s Deleted successfully", taxId), taxDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
