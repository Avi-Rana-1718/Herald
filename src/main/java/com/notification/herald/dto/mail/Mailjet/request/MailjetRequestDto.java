package com.notification.herald.dto.mail.Mailjet.request;

import java.util.List;

public record MailjetRequestDto(
        List<MailjetRequestMessages> Messages
) {}
