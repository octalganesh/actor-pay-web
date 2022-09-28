package com.octal.actorPay.entities;

import com.octal.actorPay.constants.EkycStatus;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


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

    @Column(name = "contact_number", unique = true, nullable = false)
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

    @Column(name = "is_merchant")
    private Boolean isMerchant;

    @OneToOne
    @JoinColumn( name = "role_id")
    private Role role;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MerchantDetails merchantDetails;

    @Column(name = "is_phone_verified")
    private Boolean phoneVerified;

    @Column(name = "is_email_verified")
    private Boolean emailVerified;

    @Column(name = "longitude")
    private Long longitude;

    @Column(name = "latitude")
    private Long latitude;

    @Column(name = "upi_qr_code")
    private String upiQrCode;

    @Column(name = "user_type")
    private String userType;

    private boolean defaultPassword;

    @Column(name = "temp")
    private String temp;

    @Column(name = "aadhar_number")
    private String aadharNumber;

    @Column(name = "pan_number")
    private String panNumber;

    public EkycStatus getEkycStatus() {
        return ekycStatus;
    }

    public void setEkycStatus(EkycStatus ekycStatus) {
        this.ekycStatus = ekycStatus;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(boolean defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUpiQrCode() {
        return upiQrCode;
    }

    public void setUpiQrCode(String upiQrCode) {
        this.upiQrCode = upiQrCode;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public void setMerchant(Boolean merchant) {
        isMerchant = merchant;
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

//    public UserOtpVerification getUserOtpVerification() {
//        return userOtpVerification;
//    }
//
//    public void setUserOtpVerification(UserOtpVerification userOtpVerification) {
//        this.userOtpVerification = userOtpVerification;
//    }

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

    public MerchantDetails getMerchantDetails() {
        return merchantDetails;
    }

    public void setMerchantDetails(MerchantDetails merchantDetails) {
        this.merchantDetails = merchantDetails;
    }

    public Boolean getMerchant() {
        return isMerchant;
    }

    public void setIsMerchant(Boolean merchant) {
        isMerchant = merchant;
    }
//
//    public List<Collaborators> getCollaborators() {
//        return collaborators;
//    }
//
//    public void setCollaborators(List<Collaborators> collaborators) {
//        this.collaborators = collaborators;
//    }

    @Override
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
                '}';
    }
}