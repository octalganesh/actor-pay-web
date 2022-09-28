package com.octal.actorPay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserDocErrorDTO implements Serializable {


    private String errorMessage;

    private String documentId;

}
