package com.octal.actorPay.entities;

import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Table(name = "kyc_verify_status")
@Entity
@Data
public class KycVerifyStatus extends AbstractPersistable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_full_name")
    private String userFullName;

    @Column(name = "verified_status")
    @Enumerated(EnumType.STRING)
    EkycStatus ekycStatus;

//    @OneToMany(mappedBy = "kycVerifyStatus", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//    private List<UserDocument> userDocuments;

    @Column(name = "error_message")
    private String errorMessage;

}
