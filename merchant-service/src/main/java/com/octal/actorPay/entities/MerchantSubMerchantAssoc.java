package com.octal.actorPay.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "merchant_submerchant")
public class MerchantSubMerchantAssoc extends AbstractPersistable {

    private String merchantId;

    private String submerchantId;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getSubmerchantId() {
        return submerchantId;
    }

    public void setSubmerchantId(String submerchantId) {
        this.submerchantId = submerchantId;
    }
}
