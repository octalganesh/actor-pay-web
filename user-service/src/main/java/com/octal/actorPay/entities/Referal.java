package com.octal.actorPay.entities;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@Entity
@Table(name = "referal")
@DynamicUpdate
public class Referal extends AbstractPersistable{

    @Column(name = "referal_code")
    private String referalCode;

    @Column(name = "invite_touser_email")
    private String inviteToUserEmail;

    @OneToOne
    @JoinColumn(name = "from_userid")
    private User fromUserId;

    @Column(name = "from_user_email")
    private String fromUserEmail;


}
