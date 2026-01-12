package com.notification.herald.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.notification.herald.dto.notification.TriggerNotificationDto;
import com.notification.herald.dto.notification.TriggerNotificationResponse;
import com.notification.herald.enums.NotificationTypeEnum;

@Service
public class NotificationService {
    public TriggerNotificationResponse triggerNotification(TriggerNotificationDto request) {
       
      UUID requestId = UUID.randomUUID();
        // push to kafka based on type
        for(NotificationTypeEnum type: request.type()) {
            if(type.equals(NotificationTypeEnum.EMAIL)) {

            } else if(type.equals(NotificationTypeEnum.SMS)) {

            }
        }
        
        TriggerNotificationResponse response = new TriggerNotificationResponse(requestId.toString());
        return response;
    }
}
