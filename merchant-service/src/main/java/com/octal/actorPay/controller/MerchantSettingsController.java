package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.MerchantSettingsDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantSettingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/settings")
public class MerchantSettingsController extends BaseController {


    private MerchantSettingService merchantSettingService;

    public MerchantSettingsController(MerchantSettingService merchantSettingService) {
        this.merchantSettingService = merchantSettingService;
    }

    @Secured("ROLE_SETTING_VIEW_BY_KEY")
    @GetMapping("/key/{key}")
    public ResponseEntity<ApiResponse> getMerchantSettingsByKey(
            @PathVariable("key") String key, @RequestParam("merchantId") String merchantId, HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        MerchantSettingsDTO merchantSettingsDTO =
                merchantSettingService.findMerchantSettingsByKey(key, merchantId);
        if (merchantSettingsDTO == null) {
            throw new RuntimeException(String.format("Merchant settings not found for given key %s ", key));
        }
        return new ResponseEntity<>(new ApiResponse("Settings Details ", merchantSettingsDTO,
                HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getMerchantSettings(@RequestParam("keys") List<String> keys
            , @RequestParam("merchantId") String merchantId,HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        List<MerchantSettingsDTO> merchantSettingsDTOList =
                merchantSettingService.getMerchantSettings(keys, merchantId);
        if (merchantSettingsDTOList == null || merchantSettingsDTOList.size() == 0) {
            throw new RuntimeException(String.format("Merchant settings not found"));
        }
        return new ResponseEntity<>(new ApiResponse("Settings Details ", merchantSettingsDTOList,
                HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SETTING_ADD")
    @PutMapping("/add")
    public ResponseEntity<ApiResponse> addMerchantSettings(
            @RequestBody List<SystemConfigurationDTO> systemConfigurationDTOS,
            @RequestParam("merchantId") String merchantId,HttpServletRequest request) {
        List<MerchantSettingsDTO> merchantSettingsDTOS = merchantSettingService.addMerchantSettings(systemConfigurationDTOS, merchantId);
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(new
                ApiResponse("Merchant Settings Added Successfully", merchantSettingsDTOS, String.valueOf(HttpStatus.OK.value()),
                HttpStatus.OK), HttpStatus.OK);

    }
}
