package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@DynamicUpdate
public class User extends AbstractPersistable {



    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "last_login")
    private LocalDateTime lastLoginDate;

    @Column(name = "invalid_login_attempts")
    private Integer invalidLoginAttempts;

    @Column(name = "extension_number")
    private String extensionNumber;

    @Column(name = "contact_number", unique = true, nullable = false)
    private String contactNumber;

    @Column(name = "profile_picture_url")
    private String profilePicture;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "is_kyc_done")
    private boolean isKycDone;

    @Column(name = "is_notification_active")
    private boolean isNotificationActive;

    @Column(name = "address", length = 500)
    private String address;

    @OneToOne
    @JoinColumn( name = "role_id")
    private Role role;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserOtpVerification userOtpVerification;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "upi_qr_code")
    private String upiQrCode;

    public String getUpiQrCode() {
        return upiQrCode;
    }

    public void setUpiQrCode(String upiQrCode) {
        this.upiQrCode = upiQrCode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public User() {
    }


    /**
     * @return the lastLoginDate
     */
    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * @param lastLoginDate the lastLoginDate to set
     */
    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    /**
     * @return the invalidLoginAttempts
     */
    public Integer getInvalidLoginAttempts() {
        return invalidLoginAttempts;
    }

    /**
     * @param invalidLoginAttempts the invalidLoginAttempts to set
     */
    public void setInvalidLoginAttempts(Integer invalidLoginAttempts) {
        this.invalidLoginAttempts = invalidLoginAttempts;
    }

    /**
     * @return the roles
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the roles to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }



    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isKycDone() {
        return isKycDone;
    }

    public void setKycDone(boolean kycDone) {
        isKycDone = kycDone;
    }

    public boolean isNotificationActive() {
        return isNotificationActive;
    }

    public void setNotificationActive(boolean notificationActive) {
        isNotificationActive = notificationActive;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserOtpVerification getUserOtpVerification() {
        return userOtpVerification;
    }

    public void setUserOtpVerification(UserOtpVerification userOtpVerification) {
        this.userOtpVerification = userOtpVerification;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {  return lastName;   }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                ", invalidLoginAttempts=" + invalidLoginAttempts +
                ", extensionNumber='" + extensionNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", isKycDone=" + isKycDone +
                '}';
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        isAdmin = admin;
    }
}