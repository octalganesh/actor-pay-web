package com.octal.actorPay.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class MerchantFeignHelper {


    private AdminFeignClient adminFeignClient;

    public MerchantFeignHelper(AdminFeignClient adminFeignClient) {
        this.adminFeignClient = adminFeignClient;
    }

    //    List<MyClass> myObjects = mapper.readValue(jsonInput, new TypeReference<List<MyClass>>(){});
    public List<SystemConfigurationDTO> getMerchantSettings(List<String> keys) {

        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminFeignClient.getMerchantSettingsConfig(keys);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            List<SystemConfigurationDTO> sysConfigs = mapper.convertValue(apiResponse.getData(),
                    new TypeReference<List<SystemConfigurationDTO>>() {
                    });
            return sysConfigs;
        }
        return null;
    }

    public SystemConfigurationDTO getConfigByKey(String key) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminFeignClient.getConfigurationByKey(key);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            SystemConfigurationDTO systemConfigurationDTO = mapper.convertValue(apiResponse.getData(), SystemConfigurationDTO.class);
            return systemConfigurationDTO;
        }
        return null;
    }

    public SystemConfigurationDTO getGlobalConfigByKey(String key) throws Exception {

//        @GetMapping(value = "/v1/system/configuration/read/by/key/{key}")
//        ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable(name = "key") String key)
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = adminFeignClient.getConfigurationByKey(key);
            if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = apiResponseResponseEntity.getBody();
                ObjectMapper mapper = new ObjectMapper();
                SystemConfigurationDTO systemConfigurationDTO = mapper.convertValue(apiResponse.getData(), SystemConfigurationDTO.class);
                return systemConfigurationDTO;
            }
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String content = e.contentUTF8();
            Collection<ApiResponse> apiResponse = objectMapper.readValue(
                    content, new TypeReference<Collection<ApiResponse>>() {
                    });
            System.out.println(apiResponse.stream().findFirst());
            System.out.println(e.getMessage());
            apiResponse.stream().findFirst().get();
            throw new RuntimeException(apiResponse.stream().findFirst().get().getMessage());
        }
        return null;
    }
}
