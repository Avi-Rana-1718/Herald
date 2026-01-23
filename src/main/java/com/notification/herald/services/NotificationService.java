package com.notification.herald.services;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.NotificationTypeEnum;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.MailUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    private MailUtil mailUtil;

    private NotificationRepository notificationRepository;

    NotificationService(MailUtil mailUtil, NotificationRepository notificationRepository) {
        this.mailUtil = mailUtil;
        this.notificationRepository = notificationRepository;
    }

    public ResponseEntity<ResponseDto> triggerEmail(MailRequestDto request) throws Exception {
      UUID requestId = UUID.randomUUID();
      ResponseDto response = new ResponseDto(requestId, HttpStatus.CREATED.value());

      String referenceId = mailUtil.sendMail(request, MailProviderEnum.MAILJET);
      NotificationEntity notification = new NotificationEntity(requestId, referenceId, requestId, NotificationTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 0);
      notificationRepository.save(notification);

      return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.status()));
    }
}
