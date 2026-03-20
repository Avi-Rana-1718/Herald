package com.notification.herald.dto.otp;

import jakarta.validation.constraints.NotBlank;

public record OtpRequestDto(
       String toMobile,
       String toEmail,
       @NotBlank
       String content,
       String recipientName,
       Integer expiresIn
) {}
