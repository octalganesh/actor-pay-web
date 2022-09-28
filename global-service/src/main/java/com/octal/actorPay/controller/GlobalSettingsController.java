package com.octal.actorPay.controller;

import com.octal.actorPay.configs.GlobalSettingsConfig;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.GlobalSettingsDTO;
import com.octal.actorPay.service.GlobalSettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/global/setting")
public class GlobalSettingsController extends PagedItemsController {

    private GlobalSettingsService globalSettingsService;
    private GlobalSettingsConfig globalSettingsConfig;

    public GlobalSettingsController(GlobalSettingsService globalSettingsService,
                                    GlobalSettingsConfig globalSettingsConfig) {
        this.globalSettingsService = globalSettingsService;
        this.globalSettingsConfig = globalSettingsConfig;
    }

    @GetMapping(value = "/read")
    public ResponseEntity getGlobalSettings() {
        return new ResponseEntity<>(new ApiResponse("Global Settings Data",
                globalSettingsService.getGlobalSettings(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/create/or/update")
    public ResponseEntity<ApiResponse> createOrUpdateGS(@RequestBody @Valid GlobalSettingsDTO gsDTO) {
        globalSettingsService.createOrUpdateGlobalSettings(gsDTO);
        return new ResponseEntity<>(new ApiResponse("Global Settings updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/getConfig")
    public ResponseEntity<ApiResponse> getGlobalConfig(@RequestBody String key) {


        String keyValue = globalSettingsConfig.getConfig(key);
        return new ResponseEntity(new ApiResponse("Cancellation Charge",
                keyValue, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
    }

}