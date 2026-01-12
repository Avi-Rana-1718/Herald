package com.notification.herald.enums;

public enum NotificationTypeEnum {
    SMS("sms"), EMAIL("email");
    
    private final String type;

    NotificationTypeEnum(String type) {
        this.type = type;
    }

    public String getType() { return type; }
}
