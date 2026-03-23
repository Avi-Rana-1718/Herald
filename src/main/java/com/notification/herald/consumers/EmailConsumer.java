package com.notification.herald.consumers;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.services.CommonPersistanceService;
import com.notification.herald.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final MailUtil mailUtil;

    private final String FAILED_REFERENCE = "FAILED_REFERENCE";

    @KafkaListener(topics = "EMAIL")
    public void emailConsumer(EventDto request, @Header(KafkaHeaders.DELIVERY_ATTEMPT) Integer deliveryAttempt) throws Exception {
        String requestId = request.requestId();
        MailRequestDto payload = new MailRequestDto(request.subject(),request.content(),request.recipients());
         try {
             String referenceId = mailUtil.sendMail(payload, MailProviderEnum.MAILJET);
             CommonPersistanceService.saveOrUpdateNotification(requestId, referenceId, deliveryAttempt-1);
         } catch (Exception e) {
            CommonPersistanceService.saveOrUpdateNotification(requestId, FAILED_REFERENCE, deliveryAttempt-1);
             throw e;
         }
    }
}
