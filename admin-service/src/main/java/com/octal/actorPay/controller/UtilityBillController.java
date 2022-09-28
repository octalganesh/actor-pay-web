package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.service.SystemConfigurationService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilityBillController {

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @GetMapping("/v1/utility/bill/link")
    public ResponseEntity<ApiResponse> getUtilityBillLink() {
        SystemConfigurationDTO systemConfigurationDTO = systemConfigurationService.getSystemConfigurationDetailsByKey(CommonConstant.UTILITY_BILL_LINK_KEY);
        if(systemConfigurationDTO == null) {
            throw new ActorPayException("Utility Bill link is not available in the Admin Configuration");
        }
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationDTO,
                HttpStatus.OK), HttpStatus.OK);
    }

}
