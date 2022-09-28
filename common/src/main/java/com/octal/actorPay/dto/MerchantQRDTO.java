package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
public class MerchantQRDTO extends BaseDTO {

    private String qrId;

    @JsonIgnore
    private String upiQrCode;

    private byte[] upiQrImage;

    private String merchantUserId;

    private String merchantId;

}
