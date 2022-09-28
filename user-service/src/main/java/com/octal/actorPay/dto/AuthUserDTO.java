package com.octal.actorPay.dto;

import com.octal.actorPay.entities.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to map authenticated user details in DTO,
 * this class will be used only for authenication purpose
 */
public class AuthUserDTO {

    private String id;
    private String email;
    private String username;
    private String extensionNumber;
    private String contactNumber;
    private String password;
    private Role role;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}