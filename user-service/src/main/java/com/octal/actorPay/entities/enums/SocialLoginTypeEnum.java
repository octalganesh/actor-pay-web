package com.octal.actorPay.entities.enums;

public enum SocialLoginTypeEnum {
    GOOGLE("GOOGLE"),
    FACEBOOK("FACEBOOK"),
    TWITTER("TWITTER")
    ;

    private String status;

    private SocialLoginTypeEnum(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
