package com.octal.actorPay.dto.payments;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Notes implements Serializable {

    Map<String,String> notes;
}
