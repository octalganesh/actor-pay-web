package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.octal.actorPay.constants.CommonConstant.EMAIL_PATTERN;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegistrationRequest implements Serializable {

    private String id;
    @NotBlank
    @Size(max = 255)
    private String firstName;
    @NotBlank
    @Size(max = 255)
    private String lastName;
    private String gender;
    @NotBlank
    private String dateOfBirth;
    @NotBlank
    @Pattern(regexp = EMAIL_PATTERN, message = "Invalid email id")
    private String email;
    private String profilePicture;
    @Size(min = 3, max = 3, message = "Invalid extension number")
    private String extensionNumber;
    @Size(min = 10, max = 10, message = "Invalid contact number")
    private String contactNumber;
    private String userType;
    private Boolean isActive;
    @NotBlank
    private String password;
    @JsonProperty("aadharNumber")
    @NotBlank(message = "aadharNumber must not be blank")
    private String aadhar;
    @NotBlank
    private String panNumber;

    private String referralCode;

    private List<String> roles = new ArrayList<>();

    @JsonProperty("deviceInfo")
    private UserDeviceDetailsRequest userDeviceDetailsRequest;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public UserDeviceDetailsRequest getUserDeviceDetailsRequest() {
        return userDeviceDetailsRequest;
    }

    public void setUserDeviceDetailsRequest(UserDeviceDetailsRequest userDeviceDetailsRequest) {
        this.userDeviceDetailsRequest = userDeviceDetailsRequest;
    }
}