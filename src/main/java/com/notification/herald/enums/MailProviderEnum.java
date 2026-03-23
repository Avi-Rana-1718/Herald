package com.notification.herald.enums;

public enum MailProviderEnum {
    MAILJET("mailjet");

    private final String value;

    MailProviderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}