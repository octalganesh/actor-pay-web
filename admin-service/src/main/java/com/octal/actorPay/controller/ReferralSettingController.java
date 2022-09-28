package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReferralSettingDTO;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.ReferralSetting;
import com.octal.actorPay.service.ReferralSettingService;
import com.octal.actorPay.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Author: Nancy Chauhan
 * -Handling of Role and permissions on all modules.
 * -Modules listing is a static list of sections in project on which role and permissions need to be defined.
 */
@RestController
@RequestMapping("/referral")
public class ReferralSettingController extends PagedItemsController{

    @Autowired
    private ReferralSettingService referralSettingService;

    //Ganesh

    @PostMapping("/set-referral-setting")
    public ResponseEntity<ApiResponse> setReferralSetting(@Valid @RequestBody ReferralSettingDTO referralSettingDTO) {
        referralSettingService.updateSetting(referralSettingDTO);
        return new ResponseEntity<>(new ApiResponse("Setting Update Successfully",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/get-referral-setting")
    public ResponseEntity<ApiResponse> getReferralSetting() {
        ReferralSettingDTO referralSettingDTO = referralSettingService.getReferralSetting();
        return new ResponseEntity<>(new ApiResponse("Setting Fetch Successfully",referralSettingDTO,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/approve/{orderNo}")
    public ResponseEntity<ApiResponse> approveReferral(@PathVariable("orderNo") String orderNo) {
        ReferralSettingDTO referralSettingDTO = referralSettingService.getReferralSetting();
        return new ResponseEntity<>(referralSettingService.approveReferral(orderNo, referralSettingDTO), HttpStatus.OK);
    }

    @GetMapping("/history/paged")
    public ResponseEntity<ApiResponse> getReferralHistoryByUserId(@RequestParam(name = "userId", defaultValue = "") String userId,
                                                                  @RequestParam(defaultValue = "0") Integer pageNo,
                                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(defaultValue = "true") boolean asc,
                                                                  HttpServletRequest request) {
        return referralSettingService.getReferralHistoryByUserId(userId, pageNo, pageSize, sortBy, asc);
    }

}