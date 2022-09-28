package com.octal.actorPay.model.entities;

import com.octal.actorPay.constants.RequestMoneyStatus;
import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "request_money")
public class RequestMoney extends AbstractPersistable {


    @Column(name = "user_id")
    private String userId;

    @Column(name = "request_code")
    private String requestCode;

    @Column(name = "to_user_id")
    private String toUserId;

    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private RequestMoneyStatus requestMoneyStatus;

    @Column(name = "parent_transaction_id")
    private String parentTransactionId;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "reason")
    private String reason;

    @Column(name = "request_user_type_from")
    private String requestUserTypeFrom;

    @NotEmpty
    @Column(name = "request_user_type_to")
    private String requestUserTypeTo;

}
