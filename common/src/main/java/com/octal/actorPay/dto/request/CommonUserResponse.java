package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.dto.CommonUserDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommonUserResponse implements Serializable {

    private CommonUserDTO customerDetails;

    private CommonUserDTO merchantDetails;

}
