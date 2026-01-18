package com.notification.herald.dto.mail;

public record MailRequestDto(
        String Subject,
        String TextPart,
        String HTMLPart,
        MailRequestTo To,
        MailRequestFrom From) {
}
