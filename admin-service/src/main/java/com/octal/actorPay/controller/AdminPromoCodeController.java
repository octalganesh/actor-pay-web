package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import com.octal.actorPay.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/promoCode")
public class AdminPromoCodeController {

    @Autowired
    PromoCodeService promoCodeService;

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> applyPromoCode(@RequestBody PromoCodeFilter filterRequest) {
        try {
            ApplyPromoCodeResponse response = promoCodeService.applyPromoCode(filterRequest);
            return new ResponseEntity<>(new ApiResponse("Apply PromoCode Detail", response,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }catch (RuntimeException r){
            return new ResponseEntity<>(new ApiResponse(r.getMessage(),null,String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),HttpStatus.OK),HttpStatus.OK);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> savePromoCode(@RequestBody List<ApplyPromoCodeResponse> request) {
        promoCodeService.savePromoCode(request);
        return new ResponseEntity<>(new ApiResponse("Apply PromoCode Detail", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
    @GetMapping("/getHistory/{orderId}")
    public ResponseEntity<ApiResponse> getHistoryByOrderNo(@PathVariable("orderId") String orderId) {
        return new ResponseEntity<>(new ApiResponse("PromoCode History Detail", promoCodeService.getPromoCodeHistory(orderId),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
}
