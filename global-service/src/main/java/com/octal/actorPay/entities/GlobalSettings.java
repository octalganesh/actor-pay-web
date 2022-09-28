/**
 *
 */
package com.octal.actorPay.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nancy Chauhan
 */
@Entity
@Table(name = "global_settings")
public class GlobalSettings extends AbstractPersistable {

    @Column(name = "num_of_rec_per_page", nullable = false)
    private int numOfRecPerPage;

    @Column(name = "min_amt_trans_to_user", nullable = false)
    private BigDecimal minAmtTransToUser;

    @Column(name = "max_amt_trans_to_user", nullable = false)
    private BigDecimal maxAmtTransToUser;

    @Column(name = "min_amt_trans_to_bank", nullable = false)
    private BigDecimal minAmtTransToBank;

    @Column(name = "max_amt_trans_to_bank", nullable = false)
    private BigDecimal maxAmtTransToBank;

    @Column(name = "is_ecommerce", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isECommerce;

    @Column(name = "is_remittance", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isRemittance;

    @Column(name = "is_nfc", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isNFC;

    @Column(name = "is_recharge_mobile", nullable = false, columnDefinition = "BOOLEAN")
    private Boolean isRechargeMobile;

    @Column(name = "is_utility_bill", nullable = false,columnDefinition = "BOOLEAN")
    private Boolean isUtilityBillPay;

    @Column(name = "admin_commission_all_trans", nullable = false)
    private BigDecimal adminCommissionAllTrans;

    @Column(name = "admin_commission_on_shopping", nullable = false)
    private BigDecimal adminCommissionOnShopping;

    @Column(name = "mail_from", nullable = false)
    private String mailFrom;

    @Column(name = "mail_to", nullable = false)
    private String mailTo;

    @Column(name = "server_name", nullable = false)
    private String serverName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "password", nullable = false)
    //@JsonIgnore
    private String password;

    @Column(name = "port", nullable = false)
    private int port;

    private LocalDateTime updatedAt;

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

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}