package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.FaqDTO;
import com.octal.actorPay.dto.LRPointsDTO;
import com.octal.actorPay.entities.FAQ;
import com.octal.actorPay.entities.LRP;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class LrpTransformer {
    public static Function<LRP, LRPointsDTO> LRP_TO_DTO = (lrp) -> {

        LRPointsDTO lrpDTO = new LRPointsDTO();

        lrpDTO.setId(lrp.getId());
        lrpDTO.setAddMoney(lrp.getAddMoney());
        lrpDTO.setPayTransferToWallet(lrp.getPayTransferToWallet());
        lrpDTO.setPayNFC(lrp.getPayNFC());
        lrpDTO.setBuyDeals(lrp.getBuyDeals());
        lrpDTO.setThresholdValueToRedeemPoints(lrp.getThresholdValueToRedeemPoints());
        lrpDTO.setConversionRateOfLoyaltyPoints(lrp.getConversionRateOfLoyaltyPoints());
        lrpDTO.setConversionRateOfLoyaltyCurrency(lrp.getConversionRateOfLoyaltyCurrency());
        lrpDTO.setUpdatedAt(lrp.getUpdatedAt());

        return lrpDTO;
    };

    /*
    public static Function<User, AuthUserDTO> USER_TO_AUTH_DTO = (user) -> {

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(user.getId());
        authUserDTO.setContactNumber(user.getContactNumber());
        authUserDTO.setEmail(user.getEmail());
        authUserDTO.setExtensionNumber(user.getExtensionNumber());
        authUserDTO.setPassword(user.getPassword());

        return authUserDTO;
    };
     */
}