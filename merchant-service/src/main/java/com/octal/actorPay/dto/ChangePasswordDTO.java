package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;

/**
 * @author Naveen
 */
public class ChangePasswordDTO {
    private String userId;
//    @NotBlank(message = "{current.password.is.required}")
    private String currentPassword;
    @NotBlank
    private String newPassword;
    @NotBlank
    private String confirmPassword;

    private String token;

    public ChangePasswordDTO() {
    }

    public ChangePasswordDTO(@NotBlank String currentPassword,
                             @NotBlank String newPassword,
                             @NotBlank String confirmPassword) {
        super();
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}