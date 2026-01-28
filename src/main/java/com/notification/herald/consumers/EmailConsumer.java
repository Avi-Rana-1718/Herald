package com.notification.herald.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.NotificationTypeEnum;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.MailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    private MailUtil mailUtil;
    private NotificationRepository notificationRepository;

    EmailConsumer(MailUtil mailUtil, NotificationRepository notificationRepository) {
        this.mailUtil = mailUtil;
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "emails")
    public void emailConsumer(EventDto request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MailRequestDto payload = mapper.convertValue(request.payload(), MailRequestDto.class);
        String referenceId = mailUtil.sendMail(payload, MailProviderEnum.MAILJET);
        NotificationEntity notification = new NotificationEntity(request.requestId(), referenceId, request.userId(), NotificationTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0);
        notificationRepository.save(notification);
    }


}
