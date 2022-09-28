package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ContactRequest implements Serializable {

    private String name;

    private String email;

    private String contact;

    private String type;

    private String reference_id;

}
