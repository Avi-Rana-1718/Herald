package com.notification.herald.providers.mail;

import com.notification.herald.dto.mail.MailRequestDto;
import reactor.core.publisher.Mono;

public interface MailProvider {
    // return requestId
    Mono<String> sendMail(MailRequestDto request);
    void setStatus(String requestId);
}
