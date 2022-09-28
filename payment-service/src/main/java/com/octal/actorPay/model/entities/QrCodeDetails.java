package com.octal.actorPay.model.entities;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "qr_code_details")
public class QrCodeDetails extends AbstractPersistable {

    @Column(name = "qr_code_id")
    private String qrCodeId;

    @Column(name = "ifsc")
    private String ifsc;

    @Column(name ="account_holder_name")
    private String accountHolderName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "fund_account_id")
    private String fundAccountId;

    @Column(name = "upi_id")
    private String upiId;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;

}
