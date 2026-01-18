package com.notification.herald.dto.mail;

import jakarta.validation.constraints.NotNull;

public record MailAddress(
        @NotNull
        String email,
        @NotNull
        String name
) {}