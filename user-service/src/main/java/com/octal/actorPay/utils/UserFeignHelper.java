package com.octal.actorPay.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.feign.clients.MerchantClient;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class UserFeignHelper {

    private MerchantClient merchantClient;

    private AdminClient adminClient;

    public UserFeignHelper(MerchantClient merchantClient, AdminClient adminClient) {
        this.merchantClient = merchantClient;
        this.adminClient = adminClient;
    }

    public List<MerchantSettingsDTO> getMerchantSettings(List<String> keys, String merchantId) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getMerchantSettings(keys, merchantId);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            List<MerchantSettingsDTO> merchantSettingsDTOS = mapper.convertValue(apiResponse.getData(),
                    new TypeReference<List<MerchantSettingsDTO>>() {
                    });
            return merchantSettingsDTOS;
        }
        return null;
    }

    public List<SystemConfigurationDTO> getGlobalSettingsDefaultMerchantSettings() {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminClient.getMerchantDefaultSettingsConfig();
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            List<SystemConfigurationDTO> merchantSettingsDTOS = mapper.convertValue(apiResponse.getData(),
                    new TypeReference<List<SystemConfigurationDTO>>() {
                    });
            return merchantSettingsDTOS;
        }
        return null;
    }

    public SystemConfigurationDTO getGlobalConfigByKey(String key) throws Exception {

//        @GetMapping(value = "/v1/system/configuration/read/by/key/{key}")
//        ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable(name = "key") String key)
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = adminClient.getConfigurationByKey(key);
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

    public List<SystemConfigurationDTO> getGlobalConfigByKeys(List<String> keys) throws Exception {
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = adminClient.readByKeys(keys);
            if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = apiResponseResponseEntity.getBody();
                ObjectMapper mapper = new ObjectMapper();
                List<SystemConfigurationDTO> sysconfigs = mapper.convertValue(apiResponse.getData(),
                        new TypeReference<List<SystemConfigurationDTO>>() {
                        });
                return sysconfigs;
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

    public MerchantSettingsDTO getConfigByKey(String key, String merchantId) throws Exception {
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getMerChantSettingsByKey(key, merchantId);
            if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = apiResponseResponseEntity.getBody();
                ObjectMapper mapper = new ObjectMapper();
                MerchantSettingsDTO merchantSettingsDTO = mapper.convertValue(apiResponse.getData(), MerchantSettingsDTO.class);
                return merchantSettingsDTO;
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

    public ProductDTO getProductById(String productId) throws Exception {
        ProductDTO productDTO = null;
        ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.getProductById(productId);
        ApiResponse productBodyData = apiResponseResponseEntity.getBody();
        if (productBodyData.getData() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            productDTO = objectMapper.convertValue(productBodyData.getData(), ProductDTO.class);
        }
        return productDTO;
    }

    public ResponseEntity<ApiResponse> getProductName(String productName) throws Exception {
        try {
            ResponseEntity<ApiResponse> responseEntity = merchantClient.findByName(productName);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = responseEntity.getBody();
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Product not found", "", HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException feignException) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(feignException);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ApiResponse> getProductNameById(String productId) {
        try {
            ResponseEntity<String> responseEntity = merchantClient.getProductName(productId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String productName = responseEntity.getBody();
                return new ResponseEntity<ApiResponse>(new ApiResponse("Product Name", productName, HttpStatus.OK.name(),
                        HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Product not found", "", HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException feignException) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(feignException);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ApiResponse> getMerchantNameById(String merchantId) throws Exception {
        try {
            ResponseEntity<String> responseEntity = merchantClient.getMerchantName(merchantId);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String merchantName = responseEntity.getBody();
                return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Name", merchantName, HttpStatus.OK.name(),
                        HttpStatus.OK), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Merchant id not found", "", HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException feignException) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(feignException);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    public boolean checkCancellationTime(OrderItem orderItem) {
        try {
            MerchantSettingsDTO merchantSettingsDTO = getConfigByKey(CommonConstant.RETURN_DAYS, orderItem.getMerchantId());
            int daysConfig = Integer.parseInt(merchantSettingsDTO.getParamValue());
            LocalDateTime localDateTime = orderItem.getCreatedAt();
            LocalDateTime currentDateTime = LocalDateTime.now();
            long days = ChronoUnit.DAYS.between(localDateTime, currentDateTime);
            if (days > daysConfig && orderItem.getOrderItemStatus().
                    equalsIgnoreCase(OrderStatus.DELIVERED.name())) {
                return true;
            } else {
                return false;
            }
        } catch (FeignException fe) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            System.out.println("API Error " + apiResponse.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public MerchantDTO getMerchantByMerchantId(String merchantId) {
        ResponseEntity<ApiResponse> merResponseEntity = merchantClient.getMerchantByMerchantId(merchantId);
        ApiResponse apiResponse = merResponseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        return merchantDTO;
    }

    public MerchantDTO getMerchantByUserId(String userId) {
        ResponseEntity<ApiResponse> responseEntity = merchantClient.getMerchantByUserId(userId);
        ApiResponse apiResponse = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        return merchantDTO;
    }

    //    @GetMapping(value = "/admin/by/email/{email}")
    public LinkedHashMap<String, Object> geAdminUserByEmailId(String email) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminClient.getUserByEmailId(email);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) apiResponse.getData();
            System.out.println("User Data " + data);

            return data;
        }
        return null;
    }

    public AdminDTO getUserDetailsById(@PathVariable(name = "id") String id) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminClient.getUserById(id);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            AdminDTO adminDTO = mapper.convertValue(apiResponse.getData(), AdminDTO.class);
            return adminDTO;
        }
        return null;
    }

    public AdminDTO getAdminUserId(String adminUserId) {
        AdminDTO adminDTO = getUserDetailsById(adminUserId);
        return adminDTO;
    }

    public MerchantDTO getMerchantId(String merchantId) {
        MerchantDTO merchantDTO = getMerchantByMerchantId(merchantId);
        return merchantDTO;
    }

    public List<MerchantSettingsDTO> addMerchantSettings(List<SystemConfigurationDTO> systemConfigurations, String mechantId) {
        List<MerchantSettingsDTO> merchantSettingsDTOList = null;
        ResponseEntity<ApiResponse> responseEntity = merchantClient.addMerchantSettings(systemConfigurations, mechantId);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            merchantSettingsDTOList = mapper.convertValue(apiResponse.getData(),
                    new TypeReference<List<MerchantSettingsDTO>>() {
                    });
            return merchantSettingsDTOList;
        }
        return merchantSettingsDTOList;
    }

    public ResponseEntity<ApiResponse> updateProductStock(String productId, Integer stockCount, String stockStatus) {
        ResponseEntity<ApiResponse> responseEntity = merchantClient.updateProductStock(productId, stockCount, stockStatus);
        return responseEntity;
    }


}
