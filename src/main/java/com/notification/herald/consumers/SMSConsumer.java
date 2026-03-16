package com.notification.herald.consumers;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.SMSUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSConsumer {

    private final SMSUtil smsUtil;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "SMS")
    public void smsConsumer(EventDto request) {
        // todo

    }
}
