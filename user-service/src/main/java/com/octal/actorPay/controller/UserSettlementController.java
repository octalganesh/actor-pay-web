package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ProductCommissionDTO;
import com.octal.actorPay.service.ProductCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserSettlementController {

    @Autowired
    private ProductCommissionService productCommissionService;

    @GetMapping("/settlement/{status}")
    public ResponseEntity<ApiResponse> findBySettlementStatus(@PathVariable("status") String settlementStatus) {
        List<ProductCommissionDTO> productCommissionDTOList =
                productCommissionService.findBySettlementStatus(settlementStatus);
        return new ResponseEntity<>(new ApiResponse("Product Commission Details", productCommissionDTOList,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
