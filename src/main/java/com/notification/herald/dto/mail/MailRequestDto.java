package com.notification.herald.dto.mail;

import com.notification.herald.dto.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MailRequestDto(
        @NotNull
        String subject,
        @NotNull
        String content,
        @Valid
        UserDto to,
        String requestId
) {}
