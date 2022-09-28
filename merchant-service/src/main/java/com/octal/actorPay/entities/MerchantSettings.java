package com.octal.actorPay.entities;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Table(name = "merchant_settings")
@Entity
@Where(clause = "is_deleted=false")
public class MerchantSettings extends AbstractPersistable {

    @Column(name = "param_name", nullable = false)
    private String paramName;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "param_description")
    private String paramDescription;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    MerchantDetails merchantDetails;

    public MerchantDetails getMerchantDetails() {
        return merchantDetails;
    }

    public void setMerchantDetails(MerchantDetails merchantDetails) {
        this.merchantDetails = merchantDetails;
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
}
