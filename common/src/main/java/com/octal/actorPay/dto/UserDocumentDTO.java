package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDocumentDTO implements Serializable {

    private String id;

    private String idNo;

    private String docType;

    private String documentData;

    private EkycStatus ekycStatus;

    private String userId;

    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private boolean deleted = false;

    @JsonIgnore
    private Boolean active;

    private List<UserDocErrorDTO> userDocErrors;

    private String reasonDescription;

}
