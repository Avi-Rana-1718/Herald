package com.notification.herald.dto.mail.Mailjet;

import lombok.Getter;

import java.util.List;

public record MailjetResponseDto(
        List<MailjetMessages> Messages
) {}

;
