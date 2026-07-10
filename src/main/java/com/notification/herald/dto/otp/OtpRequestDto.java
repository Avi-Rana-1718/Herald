package com.notification.herald.dto.otp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpRequestDto(
    @Valid OtpEmailTarget email,
    @Valid OtpSmsTarget sms,
    @NotBlank String content,
    @NotNull(message = "expiresIn is mandatory") Integer expiresIn) {}
