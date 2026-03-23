package com.notification.herald.consumers;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.SMSProviderEnum;
import com.notification.herald.services.CommonPersistanceService;
import com.notification.herald.utils.SMSUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSConsumer {

    private final SMSUtil smsUtil;

    private final String FAILED_REFERENCE = "FAILED_REFERENCE";

    @KafkaListener(topics = "SMS")
    public void smsConsumer(EventDto request, @Header(value = KafkaHeaders.DELIVERY_ATTEMPT) Integer deliveryAttempt) throws Exception {
        String requestId = request.requestId();
        SMSRequestDto smsRequestDto = new SMSRequestDto(request.recipients().getFirst().phoneNumber(), request.content());

        try {
            String referenceId = smsUtil.sendSMS(smsRequestDto, SMSProviderEnum.TWILIO);
            CommonPersistanceService.saveOrUpdateNotification(requestId, referenceId, deliveryAttempt-1, NotifTypeEnum.SMS, NotificationStatusEnum.REQUESTED);
        } catch (Exception e) {
            CommonPersistanceService.saveOrUpdateNotification(requestId, FAILED_REFERENCE, deliveryAttempt-1, NotifTypeEnum.SMS, NotificationStatusEnum.FAILED);
            throw e;
        }
    }
}
