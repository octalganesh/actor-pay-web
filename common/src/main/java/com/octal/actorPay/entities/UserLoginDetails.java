package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

//@Entity
//@Table(name = "user_login_details")
public class UserLoginDetails extends AbstractPersistable {


    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
