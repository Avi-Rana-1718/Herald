package com.notification.herald.dto.mail.Mailjet;

import lombok.Getter;

import java.util.List;

public record MailjetMessages(
        String Status,
        List<MailjetTo> To
) {}
