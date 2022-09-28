package com.octal.actorPay.dto.request;

import com.octal.actorPay.constants.MerchantType;

import java.io.Serializable;

public class SystemConfigRequest implements Serializable {

    private String paramName;

    private String paramValue;

    private MerchantType merchantType;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
    }
}
