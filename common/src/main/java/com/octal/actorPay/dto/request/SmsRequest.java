package com.octal.actorPay.dto.request;

import java.io.Serializable;
import java.util.List;

public class SmsRequest implements Serializable {

    private String content;
    private List<String> to;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }
}
