package com.notification.herald.dto.mail.Mailjet.response;

import java.util.List;

public record MailjetMessages(
        String Status,
        List<MailjetTo> To
) {}
