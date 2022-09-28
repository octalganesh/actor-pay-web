package com.octal.actorPay.dto;


import lombok.Data;

@Data
public class AdminDashboardDTO{
    private long totalUsers;
    private long totalActiveUsers;
    private long totalInActiveUsers;
    private long totalVendors;
    private long totalTransaction;
    private long totalTransactionUnderCategory;
    private long totalProducts;
    private Float totalEarning;
}
