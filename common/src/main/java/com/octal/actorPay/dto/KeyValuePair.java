package com.octal.actorPay.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyValuePair {
    private String key;
    private String value;

    private String secondaryValue;
    private Boolean status;

    public KeyValuePair() {
    }

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValuePair(String key, String value, String secondaryValue) {
        this.key = key;
        this.value = value;
        this.secondaryValue = secondaryValue;
    }

    public KeyValuePair(String key, String value, Boolean status) {
        this.key = key;
        this.value = value;
        this.status = status;
    }
}