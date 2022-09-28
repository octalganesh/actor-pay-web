package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactResponse implements Serializable {

    @JsonProperty("id")
    private String contactId;

    private String name;

    private String type;

    private String email;

    @JsonProperty("reference_id")
    private String referenceId;

    @JsonProperty("contact")
    private String phone;

    private boolean active;
}
