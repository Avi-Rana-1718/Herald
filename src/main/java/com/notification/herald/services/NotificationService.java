package com.notification.herald.services;

import java.util.UUID;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.utils.MailUtil;
import org.springframework.stereotype.Service;

import com.notification.herald.dto.notification.TriggerNotificationDto;
import com.notification.herald.dto.notification.TriggerNotificationResponse;
import com.notification.herald.enums.NotificationTypeEnum;

@Service
public class NotificationService {

    private MailUtil mailUtil;

    NotificationService(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    public String triggerEmail(MailRequestDto request) throws Exception {
       
      UUID requestId = UUID.randomUUID();
      return mailUtil.sendMail(request, MailProviderEnum.MAILJET);
    }
}
