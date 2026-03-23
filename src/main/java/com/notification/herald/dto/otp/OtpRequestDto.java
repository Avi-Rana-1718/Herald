package com.notification.herald.dto.otp;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpRequestDto(
       String toMobile,
       String toEmail,
       @NotBlank
       String content,
       String recipientName,
       @NotNull(message = "expiresIn is mandatory")
       Integer expiresIn
) {}
