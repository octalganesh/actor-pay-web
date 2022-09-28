package com.octal.actorPay.constants;

public enum StockStatus {
    IN_STOCK("IN_STOCK"),
    OUT_OF_STOCK("OUT_OF_STOCK");
    private String value;

    StockStatus(String value) {
        this.value = value;
    }
}
