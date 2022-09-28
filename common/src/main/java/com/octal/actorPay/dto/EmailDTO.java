package com.octal.actorPay.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class EmailDTO {

    private String from;
    private String mailTo;
    private String subject;
    private List<Object> attachments;
    private Map<String, Object> props;
    private String templateName;
}
