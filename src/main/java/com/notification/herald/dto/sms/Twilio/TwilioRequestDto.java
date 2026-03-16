package com.notification.herald.dto.sms.Twilio;

public record TwilioRequestDto(
        String To,
        String MessagingServiceSid,
        String Body
) {}
