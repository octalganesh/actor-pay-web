package com.octal.actorPay.dto.request;

public class SubcategoryFilterRequest {

    private String filterByName;
    private Boolean filterByIsActive;
    private String filterByCategoryName;

    public String getFilterByName() {
        return filterByName;
    }

    public void setFilterByName(String filterByName) {
        this.filterByName = filterByName;
    }

    public Boolean getFilterByIsActive() {
        return filterByIsActive;
    }

    public void setFilterByIsActive(Boolean filterByIsActive) {
        this.filterByIsActive = filterByIsActive;
    }

    public String getFilterByCategoryName() {
        return filterByCategoryName;
    }

    public void setFilterByCategoryName(String filterByCategoryName) {
        this.filterByCategoryName = filterByCategoryName;
    }
}
