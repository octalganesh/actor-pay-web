package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EkycUserResponse implements Serializable {

    private String idNo;

    private String dob;


    private String gender;

    private String message;

    private UserDocumentDTO aadhaarDocType;

    private UserDocumentDTO panDocType;
}

