package com.notification.herald.dto.mail.Mailjet.request;

import com.notification.herald.dto.mail.MailAddress;

import java.util.List;

public record MailjetRequestMessages(
        MailAddress From,
        List<MailAddress> To,
        String Subject,
        String HTMLPart
) {
}
