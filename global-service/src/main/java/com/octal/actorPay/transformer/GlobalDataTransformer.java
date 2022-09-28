package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.GlobalSettingsDTO;
import com.octal.actorPay.entities.GlobalSettings;

import java.util.function.Function;

public class GlobalDataTransformer {


    public static Function<GlobalSettings, GlobalSettingsDTO> GLOBAL_SETTINGS_TO_DTO = (gs) -> {

        GlobalSettingsDTO dto = new GlobalSettingsDTO();

        dto.setId(gs.getId());
        dto.setNumOfRecPerPage(gs.getNumOfRecPerPage());
        dto.setMinAmtTransToUser(gs.getMinAmtTransToUser());
        dto.setMaxAmtTransToUser(gs.getMaxAmtTransToUser());
        dto.setMinAmtTransToBank(gs.getMinAmtTransToBank());
        dto.setMaxAmtTransToBank(gs.getMaxAmtTransToBank());
        dto.setECommerce(gs.getECommerce());
        dto.setRemittance(gs.getRemittance());
        dto.setNFC(gs.getNFC());
        dto.setRechargeMobile(gs.getRechargeMobile());
        dto.setUtilityBillPay(gs.getUtilityBillPay());
        dto.setAdminCommissionAllTrans(gs.getAdminCommissionAllTrans());
        dto.setAdminCommissionOnShopping(gs.getAdminCommissionOnShopping());
        dto.setMailFrom(gs.getMailFrom());
        dto.setMailTo(gs.getMailTo());
        dto.setServerName(gs.getServerName());
        dto.setUserName(gs.getUserName());
        dto.setPassword(gs.getPassword());
        dto.setPort(gs.getPort());

        dto.setUpdatedAt(gs.getUpdatedAt());

        return dto;
    };
}