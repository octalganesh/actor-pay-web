package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.entities.SystemConfiguration;
import com.octal.actorPay.service.SystemConfigurationService;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v1/system/configuration")
public class SystemConfigurationController extends PagedItemsController {


    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Secured("ROLE_CONFIG_CREATE")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid SystemConfigurationDTO systemConfigurationDTO, final HttpServletRequest request) {
        SystemConfiguration systemConfiguration = systemConfigurationService.createSystemConfiguration(systemConfigurationDTO, CommonUtils.getCurrentUser(request));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration created successfully.", "SystemID : "+systemConfiguration.getId(),
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CONFIG_UPDATE")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SystemConfigurationDTO systemConfigurationDTO, final HttpServletRequest request) {
        systemConfigurationService.updateSystemConfiguration(systemConfigurationDTO, CommonUtils.getCurrentUser(request));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration updated successfully.", null,
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CONFIG_DELETE")
    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteByKeyIds(@RequestBody Map<String, List<String>> keyIds, final HttpServletRequest request) throws InterruptedException {
        systemConfigurationService.deleteSystemConfiguration(keyIds.get("ids"), CommonUtils.getCurrentUser(request));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration deleted successfully.", null,
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CONFIG_READ")
    @GetMapping(value = "/read/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id, final HttpServletRequest request) {
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationService.getSystemConfigurationDetails(id, CommonUtils.getCurrentUser(request)),
                HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CONFIG_VIEW_LIST")
    @GetMapping(value = "/get/all/paged")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "false") boolean asc, HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration list", systemConfigurationService.getAllSystemConfigurationPaged(pagedInfo, CommonUtils.getCurrentUser(request)),
                HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/read/by/key/{key}")
    public ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable("key") String key) {
        SystemConfigurationDTO systemConfigurationDTO = systemConfigurationService.getSystemConfigurationDetailsByKey(key);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationDTO,
                HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/read/by/merchant/settings")
    public ResponseEntity<ApiResponse> getMerchantSettingsConfig(@RequestParam("keys") List<String> keys) {
        List<SystemConfigurationDTO> systemConfigurationDTOs = systemConfigurationService.getSystemConfigurationDetailsByKeys(keys);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationDTOs,
                HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/read/by/merchant/default/settings")
    public ResponseEntity<ApiResponse> getMerchantSettingsConfig() {
        List<SystemConfigurationDTO> systemConfigurationDTOs =
                systemConfigurationService.getSystemConfigurationDetailsByKeys(CommonConstant.merchantSettings);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationDTOs,
                HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/read/by/keys")
    public ResponseEntity<ApiResponse> readByKeys(@RequestParam("keys") List<String> keys) {
        List<SystemConfigurationDTO> systemConfigurationDTOs = systemConfigurationService.getSystemConfigurationDetailsByKeys(keys);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("System configuration details.", systemConfigurationDTOs,
                HttpStatus.OK), HttpStatus.OK);
    }

}


