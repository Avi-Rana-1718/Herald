package com.notification.herald.consumers;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.MailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final MailUtil mailUtil;
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "EMAIL")
    public void emailConsumer(EventDto request) throws Exception {
        MailRequestDto payload = new MailRequestDto(request.subject(),request.content(),request.recipients());
        String referenceId = mailUtil.sendMail(payload, MailProviderEnum.MAILJET);
        NotificationEntity notification = new NotificationEntity(request.requestId(), referenceId, request.user(), NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0);
        notificationRepository.save(notification);
    }


}
