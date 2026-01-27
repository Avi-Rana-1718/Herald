package com.notification.herald.consumers;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.NotificationTypeEnum;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.MailUtil;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailConsumer {

    private MailUtil mailUtil;
    private NotificationRepository notificationRepository;

    EmailConsumer(MailUtil mailUtil, NotificationRepository notificationRepository) {
        this.mailUtil = mailUtil;
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "emails")
    public void emailConsumer(MailRequestDto request) throws Exception {
        String referenceId = mailUtil.sendMail(request, MailProviderEnum.MAILJET);
        UUID requestId = UUID.randomUUID();
        NotificationEntity notification = new NotificationEntity(requestId, referenceId, requestId, NotificationTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0);
        notificationRepository.save(notification);
    }


}
