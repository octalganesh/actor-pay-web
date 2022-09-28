package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.MoneyTransferLimitDTO;
import com.octal.actorPay.service.MoneyTransferLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/v1/money/transfer")
public class MoneyTransferLimitController extends PagedItemsController {

    @Autowired
    private MoneyTransferLimitService moneyTransferLimitService;

    /*************************** MoneyTransferLimit ***************************/

    @GetMapping(value = "/get/money/limit")
    public ResponseEntity getMtl() {
        return new ResponseEntity<>(new ApiResponse("Money Transfer Limit Data",
                moneyTransferLimitService.getMtlData(),String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/create/or/update")
    public ResponseEntity<ApiResponse> createOrUpdateMtl(@RequestBody @Valid MoneyTransferLimitDTO mtlDTO) {
        moneyTransferLimitService.createOrUpdateMtl(mtlDTO);
        return new ResponseEntity<>(new ApiResponse("MoneyTransferLimit updated successfully",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }
}
