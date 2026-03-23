package com.notification.herald.services;

import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CommonPersistanceService {

    static NotificationRepository notificationRepository;

   public static void saveOrUpdateNotification(String requestId, String referenceId, Integer deliveryAttempt,NotifTypeEnum type, NotificationStatusEnum status) {
       NotificationEntity notification = notificationRepository.findByID(requestId);

       if(Objects.isNull(notification)) {
           notification = new NotificationEntity(requestId, referenceId, type, status, deliveryAttempt-1);
       } else {
           notification.setRetryCount(deliveryAttempt);
           notification.setReferenceId(referenceId);
           notification.setStatus(status);
       }

       notificationRepository.save(notification);
    }

}
