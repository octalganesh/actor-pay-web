package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeactivatePgRequest implements Serializable {
    private boolean active;
}
