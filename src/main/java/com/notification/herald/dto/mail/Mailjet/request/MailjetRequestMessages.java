package com.notification.herald.dto.mail.Mailjet.request;

import com.notification.herald.dto.mail.MailAddress;

import java.util.List;

public record MailjetRequestMessages(
        MailAddress From,
        List<MailAddress> To,
        List<MailAddress> Cc,
        List<MailAddress> Bcc,
        String Subject,
        String TextPart,
        String HTMLPart
) {
}
