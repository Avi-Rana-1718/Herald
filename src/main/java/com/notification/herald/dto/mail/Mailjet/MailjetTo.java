package com.notification.herald.dto.mail.Mailjet;

public record MailjetTo (
    String Email,
    String MessageID,
    String MessageHref
){}
