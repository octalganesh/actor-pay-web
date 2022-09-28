package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.request.UserDeviceDetailsRequest;
import com.octal.actorPay.entities.enums.SocialLoginTypeEnum;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class SocialSignupDTO implements Serializable {

    private String firstName;
    private String lastName;
//    @NotBlank
    private String  email;
    private String mobile;
    private String extensionNumber;

    private String socialKey;
    private String  googleId;
    private String facebookId;
    private String  twitterId;
    private String  imageUrl;
    private String loginType;

    private String gender;
    private String dateOfBirth;

    private String aadhar;
    private String panNumber;

    private String referralCode;

//    @JsonProperty("deviceInfo")
    private UserDeviceDetailsRequest userDeviceDetailsRequest;

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @JsonProperty("deviceInfo")
    private DeviceDetailsDTO deviceDetailsDTO;


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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public DeviceDetailsDTO getDeviceDetailsDTO() {
        return deviceDetailsDTO;
    }

    public void setDeviceDetailsDTO(DeviceDetailsDTO deviceDetailsDTO) {
        this.deviceDetailsDTO = deviceDetailsDTO;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public UserDeviceDetailsRequest getUserDeviceDetailsRequest() {
        return userDeviceDetailsRequest;
    }

    public void setUserDeviceDetailsRequest(UserDeviceDetailsRequest userDeviceDetailsRequest) {
        this.userDeviceDetailsRequest = userDeviceDetailsRequest;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }
}
