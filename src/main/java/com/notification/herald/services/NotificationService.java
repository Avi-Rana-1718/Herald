package com.notification.herald.services;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.utils.MailUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {

    private MailUtil mailUtil;

    NotificationService(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    public ResponseEntity<ResponseDto> triggerEmail(MailRequestDto request) throws Exception {
      UUID requestId = UUID.randomUUID();
      ResponseDto response = new ResponseDto(requestId);

      String providerRequestId = mailUtil.sendMail(request, MailProviderEnum.MAILJET);

      return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
