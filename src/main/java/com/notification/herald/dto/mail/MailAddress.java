package com.notification.herald.dto.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record MailAddress(
        @NotNull
        @Email(message = "Enter a valid email address")
        String email,
        @NotNull
        String name
) {}