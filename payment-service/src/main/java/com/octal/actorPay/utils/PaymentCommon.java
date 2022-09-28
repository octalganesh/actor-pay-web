package com.octal.actorPay.utils;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.CommonUserDTO;

public class PaymentCommon {


    public static String setName(CommonUserDTO commonUserDTO) {
        String name = "";
        if (!commonUserDTO.getUserType().equals(CommonConstant.USER_TYPE_MERCHANT)) {
            name = new StringBuffer().append(commonUserDTO.getFirstName()).append(" , ").append(commonUserDTO.getLastName()).toString();
        } else {
            name = commonUserDTO.getBusinessName();
        }

        return name;
    }

}
