package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ReferralSettingDTO;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ReferralSettingService {
    void updateSetting(ReferralSettingDTO referralSettingDTO);
    ReferralSettingDTO getReferralSetting();

    ApiResponse approveReferral(String orderNo, ReferralSettingDTO referralSettingDTO);

    ResponseEntity<ApiResponse> getReferralHistoryByUserId(String userId, Integer pageNo, Integer pageSize, String sortBy, boolean asc);
}