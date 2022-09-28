package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Author : Nancy Chauhan
 */
public class GlobalSettingsDTO {

    private String id;

    private int numOfRecPerPage;

    private BigDecimal minAmtTransToUser;

    private BigDecimal maxAmtTransToUser;

    private BigDecimal minAmtTransToBank;

    private BigDecimal maxAmtTransToBank;

    private Boolean isECommerce;

    private Boolean isRemittance;

    private Boolean isNFC;

    private Boolean isRechargeMobile;

    private Boolean isUtilityBillPay;

    private BigDecimal adminCommissionAllTrans;

    private BigDecimal adminCommissionOnShopping;

    @NotBlank(message = "SMTP mail from is required")
    private String mailFrom;

    @NotBlank(message = "SMTP mail to is required")
    private String mailTo;

    @NotBlank(message = "SMTP server name is required")
    private String serverName;

    @NotBlank(message = "SMTP user name is required")
    private String userName;

    @NotBlank(message = "SMTP password is required")
    private String password;

    private int port;

    private LocalDateTime updatedAt;

    public GlobalSettingsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumOfRecPerPage() {
        return numOfRecPerPage;
    }

    public void setNumOfRecPerPage(int numOfRecPerPage) {
        this.numOfRecPerPage = numOfRecPerPage;
    }

    public BigDecimal getMinAmtTransToUser() {
        return minAmtTransToUser;
    }

    public void setMinAmtTransToUser(BigDecimal minAmtTransToUser) {
        this.minAmtTransToUser = minAmtTransToUser;
    }

    public BigDecimal getMaxAmtTransToUser() {
        return maxAmtTransToUser;
    }

    public void setMaxAmtTransToUser(BigDecimal maxAmtTransToUser) {
        this.maxAmtTransToUser = maxAmtTransToUser;
    }

    public BigDecimal getMinAmtTransToBank() {
        return minAmtTransToBank;
    }

    public void setMinAmtTransToBank(BigDecimal minAmtTransToBank) {
        this.minAmtTransToBank = minAmtTransToBank;
    }

    public BigDecimal getMaxAmtTransToBank() {
        return maxAmtTransToBank;
    }

    public void setMaxAmtTransToBank(BigDecimal maxAmtTransToBank) {
        this.maxAmtTransToBank = maxAmtTransToBank;
    }

    public Boolean getECommerce() {
        return isECommerce;
    }

    public void setECommerce(Boolean ECommerce) {
        isECommerce = ECommerce;
    }

    public Boolean getRemittance() {
        return isRemittance;
    }

    public void setRemittance(Boolean remittance) {
        isRemittance = remittance;
    }

    public Boolean getNFC() {
        return isNFC;
    }

    public void setNFC(Boolean NFC) {
        isNFC = NFC;
    }

    public Boolean getRechargeMobile() {
        return isRechargeMobile;
    }

    public void setRechargeMobile(Boolean rechargeMobile) {
        isRechargeMobile = rechargeMobile;
    }

    public Boolean getUtilityBillPay() {
        return isUtilityBillPay;
    }

    public void setUtilityBillPay(Boolean utilityBillPay) {
        isUtilityBillPay = utilityBillPay;
    }

    public BigDecimal getAdminCommissionAllTrans() {
        return adminCommissionAllTrans;
    }

    public void setAdminCommissionAllTrans(BigDecimal adminCommissionAllTrans) {
        this.adminCommissionAllTrans = adminCommissionAllTrans;
    }

    public BigDecimal getAdminCommissionOnShopping() {
        return adminCommissionOnShopping;
    }

    public void setAdminCommissionOnShopping(BigDecimal adminCommissionOnShopping) {
        this.adminCommissionOnShopping = adminCommissionOnShopping;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}