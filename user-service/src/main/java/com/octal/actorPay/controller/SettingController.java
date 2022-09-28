package com.octal.actorPay.controller;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.service.SettingService;
import com.octal.actorPay.service.ShippingAddressService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class SettingController extends PagedItemsController {

    @Autowired
    private SettingService settingService;


    @PostMapping(value = "/add-setting")
    public ResponseEntity<ApiResponse> addSetting(@RequestBody @Valid SettingDTO settingDTO, final HttpServletRequest request) {
        String loggedInUserEmail = request.getHeader("userName");
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Setting saved successfully",
                settingService.saveUserSetting(loggedInUserEmail, settingDTO), HttpStatus.OK),
                HttpStatus.OK);
    }

}