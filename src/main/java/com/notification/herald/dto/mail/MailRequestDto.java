package com.notification.herald.dto.mail;

import jakarta.validation.Valid;
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
        @Valid
        List<@Valid MailAddress> to,
        @Valid
        List<@Valid MailAddress> cc,
        @Valid
        List<@Valid MailAddress> bcc,
        @NotNull
        @Valid
        MailAddress from
) {}
