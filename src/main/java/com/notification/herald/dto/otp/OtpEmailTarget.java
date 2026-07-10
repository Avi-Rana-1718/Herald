package com.notification.herald.dto.otp;

import jakarta.validation.constraints.NotBlank;

public record OtpEmailTarget(@NotBlank String toEmail, String recipientName) {}
