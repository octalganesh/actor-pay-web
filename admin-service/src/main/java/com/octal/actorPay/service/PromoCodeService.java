package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import com.octal.actorPay.dto.PromoCodeDetailsDTO;
import com.octal.actorPay.dto.request.PromoCodeFilter;

import java.util.List;

public interface PromoCodeService {
    ApplyPromoCodeResponse applyPromoCode(PromoCodeFilter filterRequest) throws RuntimeException;

    void savePromoCode(List<ApplyPromoCodeResponse> request);
    List<PromoCodeDetailsDTO> getPromoCodeHistory(String orderId);
}
