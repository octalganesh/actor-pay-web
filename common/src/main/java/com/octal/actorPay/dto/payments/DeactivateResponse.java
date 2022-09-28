package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeactivateResponse implements Serializable {

    @JsonProperty("id")
    private String accountId;

    @JsonProperty("contact_id")
    private String contactId;

    @JsonProperty("active")
    private boolean active;

}
