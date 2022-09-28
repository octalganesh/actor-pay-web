package com.octal.actorPay.entities;

import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.constants.MerchantType;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "merchant_details")
public class MerchantDetails extends AbstractPersistable {

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "licence_number", nullable = false)
    private String licenceNumber;

    @Column(name = "shop_address")
    private String shopAddress;

    @Column(name = "full_address")
    private String fullAddress;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "merchantDetails", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MerchantSettings> merchantSettings;

    @Enumerated(EnumType.STRING)
    private MerchantType merchantType;

    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(name = "device_type")
    private String deviceType;
    @Column(name = "app_version")
    private String appVersion;
    @Column(name = "device_token")
    private String deviceToken;
    @Column(name = "device_data")
    private String deviceData;


    public void setMerchantType(MerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public List<MerchantSettings> getMerchantSettings() {
        return merchantSettings;
    }

    public void setMerchantSettings(List<MerchantSettings> merchantSettings) {
        this.merchantSettings = merchantSettings;
    }

    public MerchantType getMerchantType() {
        return merchantType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(String deviceData) {
        this.deviceData = deviceData;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }


}
