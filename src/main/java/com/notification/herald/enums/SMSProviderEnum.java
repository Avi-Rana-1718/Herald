package com.notification.herald.enums;

public enum SMSProviderEnum {
    TWILIO("twilio");

    private final String value;

    SMSProviderEnum(String value) {
        this.value = value;
    }

    public String getValue() {return this.value;}
}
