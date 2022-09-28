package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MerchantTransformer {

    public static Function<User, MerchantDTO> MERCHANT_TO_DTO = (user) -> {

        SubMerchantDTO merchantDTO = new SubMerchantDTO();

        merchantDTO.setId(user.getId());
        merchantDTO.setRoleId(user.getRole().getId());
        merchantDTO.setGender(user.getGender());
        merchantDTO.setContactNumber(user.getContactNumber());
        merchantDTO.setEmail(user.getEmail());
        merchantDTO.setExtensionNumber(user.getExtensionNumber());
        merchantDTO.setPanNumber(user.getPanNumber());
        merchantDTO.setAadhar(user.getAadharNumber());
        merchantDTO.setEkycStatus(user.getEkycStatus());
        merchantDTO.setDateOfBirth(user.getDateOfBirth());
        if(user.getProfilePicture() != null){
            merchantDTO.setProfilePicture(user.getProfilePicture());
        }
        if (user.getMerchantDetails() != null) {
            merchantDTO.setMerchantId(user.getMerchantDetails().getId());
            merchantDTO.setBusinessName(user.getMerchantDetails().getBusinessName());
            merchantDTO.setShopAddress(user.getMerchantDetails().getShopAddress());
            merchantDTO.setFullAddress(user.getMerchantDetails().getFullAddress());
            merchantDTO.setLicenceNumber(user.getMerchantDetails().getLicenceNumber());
            merchantDTO.setCreatedAt(user.getCreatedAt());
            merchantDTO.setUpdatedAt(user.getUpdatedAt());
            merchantDTO.setResourceType(user.getMerchantDetails().getResourceType());
            merchantDTO.setActive(user.getMerchantDetails().isActive());
            merchantDTO.setMerchantType(user.getMerchantDetails().getMerchantType());
            merchantDTO.setUserType(user.getUserType());
            ResourceType resourceType = user.getMerchantDetails().getResourceType();
            merchantDTO.setResourceType(resourceType);
            merchantDTO.setFirstName(user.getFirstName());
            merchantDTO.setLastName(user.getLastName());
            List<MerchantSettings> merchantSettingsList = user.getMerchantDetails().getMerchantSettings();
            if (merchantSettingsList != null && merchantSettingsList.size() > 0) {
                List<MerchantSettingsDTO> merchantSettingsDTOS = new ArrayList<>();
                for (MerchantSettings merchantSettings : merchantSettingsList) {
                    MerchantSettingsDTO merchantSettingsDTO = new MerchantSettingsDTO();
                    merchantSettingsDTO.setId(merchantSettings.getId());
                    merchantSettingsDTO.setParamDescription(merchantSettings.getParamDescription());
                    merchantSettingsDTO.setParamName(merchantSettings.getParamName());
                    merchantSettingsDTO.setParamValue(merchantSettings.getParamValue());
                    merchantSettingsDTO.setActive(merchantSettings.isActive());
                    merchantSettingsDTO.setCreatedAt(merchantSettings.getCreatedAt());
                    merchantSettingsDTO.setUpdatedAt(merchantSettings.getUpdatedAt());
                    merchantSettingsDTOS.add(merchantSettingsDTO);
                }
                merchantDTO.setMerchantSettingsDTOS(merchantSettingsDTOS);
            }
            merchantDTO.setDeviceToken(user.getMerchantDetails().getDeviceToken());
            merchantDTO.setDeviceType(user.getMerchantDetails().getDeviceType());
        }
        return merchantDTO;
    };

    public static Function<User, AuthUserDTO> USER_TO_AUTH_DTO = (user) -> {

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(user.getId());
        authUserDTO.setContactNumber(user.getContactNumber());
        authUserDTO.setEmail(user.getEmail());
//        authUserDTO.setUsername(user.getUsername());
        authUserDTO.setExtensionNumber(user.getExtensionNumber());
        authUserDTO.setPassword(user.getPassword());

        return authUserDTO;
    };

    public static Function<MerchantDetails, MerchantDTO> MERCHANT_ENTITY_TO_DTO = (merchantDetails) -> {

        MerchantDTO merchantDTO = new MerchantDTO();
        merchantDTO.setId(merchantDetails.getUser().getId());
        merchantDTO.setContactNumber(merchantDetails.getUser().getContactNumber());
        merchantDTO.setEmail(merchantDetails.getUser().getEmail());
        merchantDTO.setExtensionNumber(merchantDetails.getUser().getExtensionNumber());
        merchantDTO.setMerchantId(merchantDetails.getId());
        merchantDTO.setBusinessName(merchantDetails.getBusinessName());
        merchantDTO.setShopAddress(merchantDetails.getShopAddress());
        merchantDTO.setFullAddress(merchantDetails.getFullAddress());
        merchantDTO.setLicenceNumber(merchantDetails.getLicenceNumber());
        merchantDTO.setCreatedAt(merchantDetails.getCreatedAt());
        merchantDTO.setUpdatedAt(merchantDetails.getUpdatedAt());
        merchantDTO.setResourceType(merchantDetails.getResourceType());
        merchantDTO.setActive(merchantDetails.isActive());
        merchantDTO.setDeviceToken(merchantDetails.getDeviceToken());
        merchantDTO.setDeviceType(merchantDetails.getDeviceType());
        return merchantDTO;
    };


    public static Function<UserDocument, EkycFilterRequest> MERCHANT_EKYC_TO_DTO = (userDocument) -> {

        EkycFilterRequest ekycFilterRequest = new EkycFilterRequest();
        ekycFilterRequest.setEkycStatus(userDocument.getEkycStatus());
        ekycFilterRequest.setDocumentData(userDocument.getDocumentData());
        ekycFilterRequest.setDocType(userDocument.getDocType());
        ekycFilterRequest.setStartDate(userDocument.getCreatedAt());
        ekycFilterRequest.setEndDate(userDocument.getUpdatedAt());
        //ekycFilterRequest.setUser(userDocument.getUser());

        return ekycFilterRequest;
    };


    public static Function<MerchantQR, MerchantQRDTO> MERCHANT_OR_ENTITY_TO_DTO = (merchantQR) -> {

        MerchantQRDTO merchantQRDTO = new MerchantQRDTO();
        merchantQRDTO.setMerchantUserId(merchantQR.getMerchantUserId());
        merchantQRDTO.setMerchantId(merchantQR.getMerchantId());
        merchantQRDTO.setUpiQrCode(merchantQR.getUpiQrCode());
        merchantQRDTO.setUpiQrImage(merchantQR.getUpiQrImage());
        merchantQRDTO.setCreatedAt(merchantQR.getCreatedAt());
        merchantQRDTO.setQrId(merchantQR.getId());
        merchantQRDTO.setUpdatedAt(merchantQR.getUpdatedAt());
        merchantQRDTO.setActive(merchantQR.isActive());

        return merchantQRDTO;
    };

    public static Function<User, UserDTO> USER_TO_USER_DTO = (user) -> {

        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setId(user.getId());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setExtensionNumber(user.getExtensionNumber());
        userDTO.setGender(user.getGender());
        userDTO.setIsActive(user.isActive());
        userDTO.setProfilePicture(user.getProfilePicture());
        userDTO.setUserType(user.getUserType());
        return userDTO;
    };
}
