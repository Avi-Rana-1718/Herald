package com.notification.herald.services;

import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommonPersistanceService {

    private final NotificationRepository notificationRepository;

   public void saveOrUpdateNotification(String requestId, String referenceId, Integer deliveryAttempt,NotifTypeEnum type, NotificationStatusEnum status) {
       NotificationEntity notification = notificationRepository.findByID(requestId);

       if(Objects.isNull(notification)) {
           notification = new NotificationEntity(requestId, referenceId, type, status, deliveryAttempt);
       } else {
           notification.setRetryCount(deliveryAttempt);
           notification.setReferenceId(referenceId);
           notification.setStatus(status);
       }

       notificationRepository.save(notification);
    }

}
