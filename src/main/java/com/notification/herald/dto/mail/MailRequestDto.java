package com.notification.herald.dto.mail;

import com.notification.herald.dto.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record MailRequestDto(
    @NotNull String subject, @NotNull String content, @Valid UserDto to, String requestId) {}
