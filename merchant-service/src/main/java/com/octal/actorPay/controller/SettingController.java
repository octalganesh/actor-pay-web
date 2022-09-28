package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SettingDTO;
import com.octal.actorPay.service.SettingService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class SettingController {

    @Autowired
    private SettingService settingService;

    @PostMapping(value = "/add-setting")
    public ResponseEntity<ApiResponse> addSetting(@Valid @RequestBody SettingDTO settingDTO, final HttpServletRequest request){
        try {
            String loggedInMerchantEmail = request.getHeader("userName");
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Setting saved successfully",
                    settingService.saveUserSetting(loggedInMerchantEmail, settingDTO), HttpStatus.OK),
                    HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
    }
}
