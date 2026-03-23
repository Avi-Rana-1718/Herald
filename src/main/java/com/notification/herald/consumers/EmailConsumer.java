package com.notification.herald.consumers;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
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
    private final CommonPersistanceService commonPersistanceService;

    private final String FAILED_REFERENCE = "FAILED_REFERENCE";

    @KafkaListener(topics = "EMAIL")
    public void emailConsumer(MailRequestDto request, @Header(KafkaHeaders.DELIVERY_ATTEMPT) Integer deliveryAttempt) throws Exception {
        String requestId = request.requestId();

         try {
             String referenceId = mailUtil.sendMail(request, MailProviderEnum.MAILJET);
             commonPersistanceService.saveOrUpdateNotification(requestId, referenceId, deliveryAttempt-1, NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED);
         } catch (Exception e) {
            commonPersistanceService.saveOrUpdateNotification(requestId, FAILED_REFERENCE, deliveryAttempt-1, NotifTypeEnum.EMAIL, NotificationStatusEnum.FAILED);
             throw e;
         }
    }
}
