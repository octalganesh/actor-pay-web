package com.octal.actorPay.entities;

import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.entities.enums.SocialLoginTypeEnum;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@DynamicUpdate
public class User extends AbstractPersistable {

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "user_name")
    private String username;

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
    private int invalidLoginAttempts;

    @Column(name = "extension_number")
    private String extensionNumber;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "profile_picture_url")
    private String profilePicture;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    private EkycStatus ekycStatus;

    @Column(name = "is_notification_active")
    private boolean isNotificationActive;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "users_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
//    private Set<Role> roles = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "password")
    private String password;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<UserOtpVerification> user OtpVerification;

    @Column(name = "user_type")
    private String userType;

    @Column(name="social_key")
    private String socialKey;

    @Column(name = "google_id")
    private String googleId;
    @Column(name = "facebook_id")
    private String facebookId;
    @Column(name = "twitter_id")
    private String twitterId;

    @Column(name = "is_phone_verified")
    private Boolean phoneVerified;

    @Column(name = "is_email_verified")
    private Boolean emailVerified;

    @Column(name = "aadhar_number")
    private String aadharNumber;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "login_type")
    private String loginType;

    @Column(name = "delete_reason")
    private String deleteReason;

    @Column(name = "referral_code")
    private String referralCode;

    @Column(name = "from_referral_code")
    private String fromReferralCode;

    @Column(name = "total_user_register_with")
    private Integer totalUserRegisterWith = 0;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDeviceDetails userDeviceDetails;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShippingAddress> shippingAddress;

    @Column(name = "upi_qr_code")
    private String upiQrCode;

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
    public int getInvalidLoginAttempts() {
        return invalidLoginAttempts;
    }

    /**
     * @param invalidLoginAttempts the invalidLoginAttempts to set
     */
    public void setInvalidLoginAttempts(int invalidLoginAttempts) {
        this.invalidLoginAttempts = invalidLoginAttempts;
    }

    public Role getRole() {
        return role;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public EkycStatus getEkycStatus() {
        return ekycStatus;
    }

    public void setEkycStatus(EkycStatus ekycStatus) {
        this.ekycStatus = ekycStatus;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSocialKey() {
        return socialKey;
    }

    public void setSocialKey(String socialKey) {
        this.socialKey = socialKey;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public UserDeviceDetails getUserDeviceDetails() {
        return userDeviceDetails;
    }

    public void setUserDeviceDetails(UserDeviceDetails userDeviceDetails) {
        this.userDeviceDetails = userDeviceDetails;
    }

    public List<ShippingAddress> getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(List<ShippingAddress> shippingAddress) {
        this.shippingAddress = shippingAddress;
    }


    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getUpiQrCode() {
        return upiQrCode;
    }

    public void setUpiQrCode(String upiQrCode) {
        this.upiQrCode = upiQrCode;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getFromReferralCode() {
        return fromReferralCode;
    }

    public void setFromReferralCode(String fromReferralCode) {
        this.fromReferralCode = fromReferralCode;
    }

    public Integer getTotalUserRegisterWith() {
        return totalUserRegisterWith;
    }

    public void setTotalUserRegisterWith(Integer totalUserRegisterWith) {
        this.totalUserRegisterWith = totalUserRegisterWith;
    }

    /*    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", lastLoginDate=" + lastLoginDate +
                ", invalidLoginAttempts=" + invalidLoginAttempts +
                ", extensionNumber='" + extensionNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", isKycDone=" + ekycStatus.name() +
                '}';
    }*/


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", lastLoginDate=" + lastLoginDate +
                ", invalidLoginAttempts=" + invalidLoginAttempts +
                ", extensionNumber='" + extensionNumber + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", fcmToken='" + fcmToken + '\'' +
                ", ekycStatus=" + ekycStatus +
                ", isNotificationActive=" + isNotificationActive +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                ", googleId='" + googleId + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", twitterId='" + twitterId + '\'' +
                ", phoneVerified=" + phoneVerified +
                ", emailVerified=" + emailVerified +
                ", aadharNumber='" + aadharNumber + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", loginType='" + loginType + '\'' +
                ", userDeviceDetails=" + userDeviceDetails +
                ", shippingAddress=" + shippingAddress +
                ", upiQrCode='" + upiQrCode + '\'' +
                '}';
    }
}