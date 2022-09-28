package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_opt_verification")
@DynamicUpdate
public class UserOtpVerification extends AbstractPersistable {


    public enum UserVerificationStatus {
        STATUS_PENDING, STATUS_VERIFIED, RESET_PASSWORD_PENDING, UPDATED_PASSWORD
    }

    public enum Types {
        FORGOT_PASSWORD, NEW_USER, PHONE_VERIFICATION
    }

    @Column(name = "otp")
    private String otp;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "extension_number")
    private String extensionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_verification_status", length = 30, nullable = false)
    private UserVerificationStatus userVerificationStatus;

    @Column(name = "token_expire_at", nullable = false)
    private LocalDateTime expiredDateTime;

    @Column(name = "token_issued_at", nullable = false)
    private LocalDateTime issuedDateTime;

    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false)
    private Types type;

    //    @OneToOne
    @Column(name = "user_id")
    private String userId;

    @Column(name = "action_count")
    private Integer actonCount;

    @Column(name = "token")
    private String token;


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public UserVerificationStatus getUserVerificationStatus() {
        return userVerificationStatus;
    }

    public void setUserVerificationStatus(UserVerificationStatus userVerificationStatus) {
        this.userVerificationStatus = userVerificationStatus;
    }

    public LocalDateTime getExpiredDateTime() {
        return expiredDateTime;
    }

    public void setExpiredDateTime(LocalDateTime expiredDateTime) {
        this.expiredDateTime = expiredDateTime;
    }

    public LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    public LocalDateTime getConfirmedDateTime() {
        return confirmedDateTime;
    }

    public void setConfirmedDateTime(LocalDateTime confirmedDateTime) {
        this.confirmedDateTime = confirmedDateTime;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getActonCount() {
        return actonCount;
    }

    public void setActonCount(Integer actonCount) {
        this.actonCount = actonCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}