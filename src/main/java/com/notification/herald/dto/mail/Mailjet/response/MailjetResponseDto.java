package com.notification.herald.dto.mail.Mailjet.response;

import java.util.List;

public record MailjetResponseDto(
        List<MailjetMessages> Messages
) {}

;
