package com.octal.actorPay.dto;

import java.io.Serializable;
import java.util.List;

public class ProductCommissionReport<T> implements Serializable {


    private static final long serialVersionUID = 1L;
    private int totalPages;
    private long totalItems;
    private List<T> items;
    private int pageNumber;
    private int pageSize;
    private String merchantSettledTotal;
    private String merchantPendingTotal;
    private String merchantCancelledTotal;
    private String adminSettledTotal;
    private String adminPendingTotal;
    private String adminCancelledTotal;


    public ProductCommissionReport(int totalPage, long itemCount, List<T> items,
                                   int pageNumber, int pageSize,
                                   String merchantSettledTotal,String merchantPendingTotal,
                                   String merchantCancelledTotal,String adminSettledTotal,
                                   String adminPendingTotal, String adminCancelledTotal) {
        super();
        this.totalPages = totalPage;
        this.totalItems = itemCount;
        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.merchantSettledTotal=merchantSettledTotal;
        this.merchantPendingTotal=merchantPendingTotal;
        this.merchantCancelledTotal=merchantCancelledTotal;
        this.adminSettledTotal=adminSettledTotal;
        this.adminPendingTotal=adminPendingTotal;
        this.adminCancelledTotal=adminCancelledTotal;

    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getMerchantSettledTotal() {
        return merchantSettledTotal;
    }

    public void setMerchantSettledTotal(String merchantSettledTotal) {
        this.merchantSettledTotal = merchantSettledTotal;
    }

    public String getMerchantPendingTotal() {
        return merchantPendingTotal;
    }

    public void setMerchantPendingTotal(String merchantPendingTotal) {
        this.merchantPendingTotal = merchantPendingTotal;
    }

    public String getMerchantCancelledTotal() {
        return merchantCancelledTotal;
    }

    public void setMerchantCancelledTotal(String merchantCancelledTotal) {
        this.merchantCancelledTotal = merchantCancelledTotal;
    }

    public String getAdminSettledTotal() {
        return adminSettledTotal;
    }

    public void setAdminSettledTotal(String adminSettledTotal) {
        this.adminSettledTotal = adminSettledTotal;
    }

    public String getAdminPendingTotal() {
        return adminPendingTotal;
    }

    public void setAdminPendingTotal(String adminPendingTotal) {
        this.adminPendingTotal = adminPendingTotal;
    }

    public String getAdminCancelledTotal() {
        return adminCancelledTotal;
    }

    public void setAdminCancelledTotal(String adminCancelledTotal) {
        this.adminCancelledTotal = adminCancelledTotal;
    }
}
