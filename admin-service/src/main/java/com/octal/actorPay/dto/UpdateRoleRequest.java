package com.octal.actorPay.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class UpdateRoleRequest implements Serializable {

    private String roleId;

    private String userId;

}
