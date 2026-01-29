package com.notification.herald.providers.mail;

import com.notification.herald.dto.mail.MailRequestDto;

public interface MailProvider {

    String sendMail(MailRequestDto request);
    void setStatus(String requestId);
}
