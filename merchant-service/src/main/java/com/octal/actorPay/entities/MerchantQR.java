package com.octal.actorPay.entities;

import javax.persistence.*;

@Entity
@Table(name = "merchant_qr")
public class MerchantQR extends AbstractPersistable {

    private String upiQrCode;

    @Lob
    private byte[] upiQrImage;

    private String merchantUserId;

    private String merchantId;

    public String getMerchantUserId() {
        return merchantUserId;
    }

    public void setMerchantUserId(String merchantUserId) {
        this.merchantUserId = merchantUserId;
    }

    public String getUpiQrCode() {
        return upiQrCode;
    }

    public void setUpiQrCode(String upiQrCode) {
        this.upiQrCode = upiQrCode;
    }

    public byte[] getUpiQrImage() {
        return upiQrImage;
    }

    public void setUpiQrImage(byte[] upiQrImage) {
        this.upiQrImage = upiQrImage;
    }


    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
