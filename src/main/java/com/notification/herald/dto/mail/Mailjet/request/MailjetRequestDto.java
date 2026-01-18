package com.notification.herald.dto.mail.Mailjet.request;

import lombok.Builder;

import java.util.List;

@Builder
public record MailjetRequestDto(
        List<MailjetRequestMessages> Messages
) {}
