package com.octal.actorPay.dto;

public class CountriesDTO extends DemographicDTO {

    private String countryFlag;

    private String code;

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
