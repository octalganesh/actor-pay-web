package com.octal.actorPay.dto;

import com.octal.actorPay.constants.EkycStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private String id;
    private String email;
    // private String username;
    private LocalDateTime lastLoginDate;
    private int invalidLoginAttempts;
    private String extensionNumber;
    private String contactNumber;
    private String profilePicture;
    private String password;
    private boolean isActive;
    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String userType;

    private EkycStatus aadhaarVerifyStatus;
    private  EkycStatus panVerifyStatus;
    private EkycStatus ekycVerifyStatus;

    public EkycStatus getAadhaarVerifyStatus() {
        return aadhaarVerifyStatus;
    }

    public void setAadhaarVerifyStatus(EkycStatus aadhaarVerifyStatus) {
        this.aadhaarVerifyStatus = aadhaarVerifyStatus;
    }

    public EkycStatus getPanVerifyStatus() {
        return panVerifyStatus;
    }

    public void setPanVerifyStatus(EkycStatus panVerifyStatus) {
        this.panVerifyStatus = panVerifyStatus;
    }

    public EkycStatus getEkycVerifyStatus() {
        return ekycVerifyStatus;
    }

    public void setEkycVerifyStatus(EkycStatus ekycVerifyStatus) {
        this.ekycVerifyStatus = ekycVerifyStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public int getInvalidLoginAttempts() {
        return invalidLoginAttempts;
    }

    public void setInvalidLoginAttempts(int invalidLoginAttempts) {
        this.invalidLoginAttempts = invalidLoginAttempts;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public List<String> getRoles() {
//        return roles;
//    }

//    public void setRoles(List<String> roles) {
//        this.roles = roles;
//    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
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
}
