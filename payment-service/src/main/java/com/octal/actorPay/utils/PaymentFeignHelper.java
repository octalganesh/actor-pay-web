package com.octal.actorPay.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.PaymentAdminClient;
import com.octal.actorPay.client.PaymentMerchantClient;
import com.octal.actorPay.client.PaymentUserClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class PaymentFeignHelper {

    @Autowired
    private PaymentMerchantClient paymentMerchantClient;

    @Autowired
    private PaymentAdminClient paymentAdminClient;

    @Autowired
    private PaymentUserClient paymentUserClient;
//    public PaymentFeignHelper(PaymentMerchantClient paymentMerchantClient, PaymentAdminClient paymentAdminClient) {
//        this.paymentMerchantClient = paymentMerchantClient;
//        this.paymentAdminClient = paymentAdminClient;
//    }

    public List<MerchantSettingsDTO> getMerchantSettings(List<String> keys, String merchantId) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentMerchantClient.getMerchantSettings(keys, merchantId);
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
        ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentAdminClient.getMerchantDefaultSettingsConfig();
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
            ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentAdminClient.getConfigurationByKey(key);
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

    public MerchantSettingsDTO getConfigByKey(String key, String merchantId) throws Exception {
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentMerchantClient.getMerChantSettingsByKey(key, merchantId);
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

    public ResponseEntity<ApiResponse> getProductName(String productName) throws Exception {
        try {
            ResponseEntity<ApiResponse> responseEntity = paymentMerchantClient.findByName(productName);
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
            ResponseEntity<String> responseEntity = paymentMerchantClient.getProductName(productId);
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
            ResponseEntity<String> responseEntity = paymentMerchantClient.getMerchantName(merchantId);
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


    public MerchantDTO getMerchantByMerchantId(String merchantId) {
        ResponseEntity<ApiResponse> merResponseEntity = paymentMerchantClient.getMerchantByMerchantId(merchantId);
        ApiResponse apiResponse = merResponseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        return merchantDTO;
    }
    public MerchantDTO getMerchantById(String id) {
        ResponseEntity<ApiResponse> merResponseEntity = paymentMerchantClient.getMerchantById(id);
        ApiResponse apiResponse = merResponseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
        return merchantDTO;
    }

    //    @GetMapping(value = "/admin/by/email/{email}")
    public LinkedHashMap<String, Object> geAdminUserByEmailId(String email) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentAdminClient.getUserByEmailId(email);
        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) apiResponse.getData();
            System.out.println("User Data " + data);

            return data;
        }
        return null;
    }

    public AdminDTO getUserDetailsById(@PathVariable(name = "id") String id) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = paymentAdminClient.getUserById(id);
        ApiResponse apiResponse = apiResponseResponseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        AdminDTO adminDTO = mapper.convertValue(apiResponse.getData(), AdminDTO.class);
        return adminDTO;

//        if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
//            ApiResponse apiResponse = apiResponseResponseEntity.getBody();
//            LinkedHashMap<String, Object> data = (LinkedHashMap<String, Object>) apiResponse.getData();
//            System.out.println("User Data " + data);
//
//            return data;
//        }
//        return null;
    }

    public AdminDTO getAdminUserId(String adminUserId) {
        AdminDTO adminDTO = getUserDetailsById(adminUserId);
        return adminDTO;
    }

    public MerchantDTO getMerchantId(String merchantId) {
        MerchantDTO merchantDTO = getMerchantByMerchantId(merchantId);
        return merchantDTO;
    }
//    public MerchantDTO getMerchantById(String id) {
//        MerchantDTO merchantDTO = getMerchantByMerchantId(merchantId);
//        return merchantDTO;
//    }

    public List<MerchantSettingsDTO> addMerchantSettings(List<SystemConfigurationDTO> systemConfigurations, String mechantId) {
        List<MerchantSettingsDTO> merchantSettingsDTOList = null;
        ResponseEntity<ApiResponse> responseEntity = paymentMerchantClient.addMerchantSettings(systemConfigurations, mechantId);
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
//
//    @GetMapping(value = "/get/user/{username}")
//    public ResponseEntity getUser(@PathVariable(name = "username") String username)

    public User getCustomer(String userName) {
        ResponseEntity<ApiResponse> responseEntity = paymentUserClient.getCustomer(userName);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            User user = mapper.convertValue(responseEntity.getBody().getData(), User.class);
            return user;
        }
        return null;
    }

    public UserDTO getUserById(String userId) {
        ResponseEntity<ApiResponse> responseEntity = paymentUserClient.getUserById(userId);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            UserDTO userDTO = mapper.convertValue(apiResponse.getData(), UserDTO.class);
            return userDTO;
        }

        return null;
    }

    public MerchantDTO getMerchantUserById(String userId) {
        ResponseEntity<ApiResponse> responseEntity = paymentMerchantClient.getMerchantUser(userId);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            MerchantDTO merchantDTO = mapper.convertValue(apiResponse.getData(), MerchantDTO.class);
            return merchantDTO;
        }
        return null;
    }

    private CommonUserDTO getCommonDTO(ResponseEntity<ApiResponse> responseEntity) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            CommonUserDTO commonUserDTO = mapper.convertValue(apiResponse.getData(), CommonUserDTO.class);
            return commonUserDTO;
        }
        return null;
    }

    public CommonUserDTO getCommonUserByIdentity(String userIdentity, String userType) throws Exception {
        ResponseEntity<ApiResponse> responseEntity = null;
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
            responseEntity = paymentUserClient.getUserIdentity(userIdentity);
        }
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            responseEntity = paymentMerchantClient.getUserIdentity(userIdentity);
        }
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
            responseEntity = paymentAdminClient.getUserIdentity(userIdentity);
        }
        CommonUserDTO commonUserDTO = getCommonDTO(responseEntity);
        return commonUserDTO;
    }

    public CommonUserDTO getCommonUserByIdentity(String userIdentity) {
        ResponseEntity<ApiResponse> responseEntity = null;
        try {
            responseEntity = paymentUserClient.getUserIdentity(userIdentity);
        }catch (Exception ex) {
            try {
                responseEntity = paymentMerchantClient.getUserIdentity(userIdentity);
            }catch (Exception e) {
                responseEntity = paymentAdminClient.getUserIdentity(userIdentity);
            }
        }
//        if(responseEntity.getBody() == null)
//            responseEntity = paymentMerchantClient.getUserIdentity(userIdentity);
//        if(responseEntity.getBody() == null)
//            responseEntity = paymentAdminClient.getUserIdentity(userIdentity);
        CommonUserDTO commonUserDTO = getCommonDTO(responseEntity);
        return commonUserDTO;
    }

    public CommonUserDTO getCommonUserById(String userId, String userType) throws Exception {
        CommonUserDTO commonUser = null;
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
            UserDTO userDTO = getUserById(userId);
            commonUser = new CommonUserDTO();
            commonUser.setUserId(userDTO.getId());
            commonUser.setUserType(userDTO.getUserType());
            commonUser.setEmail(userDTO.getEmail());
            commonUser.setExtension(userDTO.getExtensionNumber());
            commonUser.setContactNumber(userDTO.getContactNumber());
            commonUser.setFirstName(userDTO.getFirstName());
            commonUser.setLastName(userDTO.getLastName());
            commonUser.setUserType(userDTO.getUserType());
        }
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            MerchantDTO merchantDTO = getMerchantUserById(userId);
            commonUser = new CommonUserDTO();
            commonUser.setUserId(merchantDTO.getId());
            commonUser.setMerchantId(merchantDTO.getMerchantId());
            commonUser.setUserType(merchantDTO.getUserType());
            commonUser.setEmail(merchantDTO.getEmail());
            commonUser.setExtension(merchantDTO.getExtensionNumber());
            commonUser.setContactNumber(merchantDTO.getContactNumber());
            commonUser.setFirstName(merchantDTO.getFirstName());
            commonUser.setLastName(merchantDTO.getLastName());
            commonUser.setBusinessName(merchantDTO.getBusinessName());
        }
        if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
            AdminDTO adminUser = getAdminUserId(userId);
            commonUser = new CommonUserDTO();
            commonUser.setUserId(adminUser.getId());
            commonUser.setUserType(userType);
            commonUser.setEmail(adminUser.getEmail());
            commonUser.setExtension(adminUser.getExtensionNumber());
            commonUser.setContactNumber(adminUser.getContactNumber());
            commonUser.setFirstName(adminUser.getFirstName());
            commonUser.setLastName(adminUser.getLastName());
        }
        return commonUser;
    }

}
