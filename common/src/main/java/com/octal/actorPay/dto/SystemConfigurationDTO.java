package com.octal.actorPay.dto;


import com.octal.actorPay.constants.SettingsType;

import javax.validation.constraints.NotBlank;

public class SystemConfigurationDTO extends BaseDTO {

    private String id;

    @NotBlank
    private String paramName;

    @NotBlank
    private String paramValue;

    @NotBlank
    private String paramDescription;

    /**
     * micro service name
     */
    private String module;

    private SettingsType settingsType;

    public SettingsType getSettingsType() {
        return settingsType;
    }

    public void setSettingsType(SettingsType settingsType) {
        this.settingsType = settingsType;
    }

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

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
