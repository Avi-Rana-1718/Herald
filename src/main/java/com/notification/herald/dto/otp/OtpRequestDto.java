package com.notification.herald.dto.otp;

public record OtpRequestDto(
       String toMobile,
       String toEmail,
       String content,
       String recipientName,
       Integer expiresIn
) {}
