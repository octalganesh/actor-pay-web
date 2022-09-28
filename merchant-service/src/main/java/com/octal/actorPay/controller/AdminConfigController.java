package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.utils.MerchantFeignHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminConfigController {

    private MerchantFeignHelper merchantFeignHelper;

    public AdminConfigController(MerchantFeignHelper merchantFeignHelper) {
        this.merchantFeignHelper = merchantFeignHelper;
    }

    @GetMapping(value = "/read/by/key/{key}")
    public ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable("key") String key) {
        SystemConfigurationDTO systemConfigurationDTO = merchantFeignHelper.getConfigByKey(key);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Config ",
                systemConfigurationDTO, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/read/by/merchant/settings")
    public ResponseEntity<ApiResponse> getMerchantSettingsConfig(@RequestParam("keys") List<String> keys) {
        List<SystemConfigurationDTO> systemConfigurationDTOS = merchantFeignHelper.getMerchantSettings(keys);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Settings ",
                systemConfigurationDTOS, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
    }
}
