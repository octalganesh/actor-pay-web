package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ReferralSettingDTO;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.ReferralSettingService;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class ReferralSettingServiceImpl implements ReferralSettingService {

    @Autowired
    private ReferralSettingRepository referralSettingRepository;

    @Autowired
    private AdminUserService adminUserService;

    @Override
    public void updateSetting(ReferralSettingDTO referralSettingDTO) {
        ReferralSetting record;
        Optional<ReferralSetting> referralSetting  = referralSettingRepository.findByCreatedBy("ADMIN");
        if(referralSetting.isPresent()){
            record = referralSetting.get();
        }else{
            record = new ReferralSetting();
            record.setCreatedBy("ADMIN");
        }
        record.setType(referralSettingDTO.getType());
        record.setOrderNumber(referralSettingDTO.getOrderNumber());
        record.setMinOrderValue(referralSettingDTO.getMinOrderValue());
        record.setMaxDiscount(referralSettingDTO.getMaxDiscount());
        if (referralSettingDTO.getType().equalsIgnoreCase("Percentage")) {
            record.setDiscountInPercentage(referralSettingDTO.getDiscount());
        } else {
            record.setDiscountInPrice(referralSettingDTO.getDiscount());
        }
        referralSettingRepository.save(record);
    }

    @Override
    public ReferralSettingDTO getReferralSetting() {
        Optional<ReferralSetting> referralSetting  = referralSettingRepository.findByCreatedBy("ADMIN");
        ReferralSettingDTO referralSettingDTO = new ReferralSettingDTO();
        if(referralSetting.isPresent()){
            referralSettingDTO.setType(referralSetting.get().getType());
            referralSettingDTO.setMaxDiscount(referralSetting.get().getMaxDiscount());
            referralSettingDTO.setMinOrderValue(referralSetting.get().getMinOrderValue());
            referralSettingDTO.setOrderNumber(referralSetting.get().getOrderNumber());
            if (referralSettingDTO.getType().equalsIgnoreCase("Percentage")) {
                referralSettingDTO.setDiscount(referralSetting.get().getDiscountInPercentage());
            } else {
                referralSettingDTO.setDiscount(referralSetting.get().getDiscountInPrice());
            }
        }
        return referralSettingDTO;
    }

    @Override
    public ApiResponse approveReferral(String orderNo, ReferralSettingDTO referralSettingDTO) {
        return adminUserService.applyReferral(orderNo, referralSettingDTO);
    }

    @Override
    public ResponseEntity<ApiResponse> getReferralHistoryByUserId(String userId, Integer pageNo, Integer pageSize, String sortBy, boolean asc) {
        return adminUserService.getReferralHistoryByUserId(userId, pageNo, pageSize, sortBy, asc);
    }
}