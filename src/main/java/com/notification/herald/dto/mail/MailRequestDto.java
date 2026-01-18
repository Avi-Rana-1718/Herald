package com.notification.herald.dto.mail;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MailRequestDto(
        @NotNull
        String subject,
        @NotNull
        String textPart,
        @NotNull
        String HTMLPart,
        @NotNull
        List<MailAddress> to,
        List<MailAddress> cc,
        List<MailAddress> bcc,
        @NotNull
        MailAddress from
) {}
