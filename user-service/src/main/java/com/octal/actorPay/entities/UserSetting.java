package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "user_setting")
@Data
public class UserSetting extends AbstractPersistable {

    @Column(name = "notification")
    private Boolean notification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
