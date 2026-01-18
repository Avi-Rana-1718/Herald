package com.notification.herald.dto.mail.Mailjet.response;

public record MailjetTo (
    String Email,
    String MessageID,
    String MessageHref
){}
