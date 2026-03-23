package com.notification.herald.dto.sms;

public record SMSRequestDto(
        String toMobile,
        String body
) {}
