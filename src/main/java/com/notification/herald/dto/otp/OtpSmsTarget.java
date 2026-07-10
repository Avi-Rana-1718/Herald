package com.notification.herald.dto.otp;

import jakarta.validation.constraints.NotBlank;

public record OtpSmsTarget(@NotBlank String toMobile) {}
