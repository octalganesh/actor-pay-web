package com.octal.actorPay.model.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "pg_user_account")
@Data
public class PgUserAccount implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "pg_contact_id")
    private String pgContactId;

    @Column(name = "pg_fund_id")
    private String pgFundId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "is_self")
    private Boolean isSelf;

    @Column(name = "is_primary_account")
    private Boolean isPrimaryAccount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private Boolean active;
}
