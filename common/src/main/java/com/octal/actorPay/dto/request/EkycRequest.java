package com.octal.actorPay.dto.request;

import com.octal.actorPay.entities.User;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class EkycRequest implements Serializable {

    @NotEmpty
    private String idNo;

    private String verificationType;

    private User user;

    private String dob;


}
