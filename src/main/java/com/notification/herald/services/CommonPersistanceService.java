package com.notification.herald.services;

import com.notification.herald.entities.InAppNotificationEntity;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.InAppNotificationRepository;
import com.notification.herald.repository.NotificationRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommonPersistanceService {

  private final NotificationRepository notificationRepository;
  private final InAppNotificationRepository inAppNotificationRepository;

  @Transactional
  public void saveOrUpdateNotification(
      String requestId,
      String referenceId,
      String sentTo,
      Integer deliveryAttempt,
      NotifTypeEnum type,
      NotificationStatusEnum status) {
    NotificationEntity notification = notificationRepository.findByID(requestId);

    if (Objects.isNull(notification)) {
      notification =
          new NotificationEntity(requestId, referenceId, sentTo, type, status, deliveryAttempt);
    } else {
      notification.setRetryCount(deliveryAttempt);
      notification.setReferenceId(referenceId);
      notification.setSentTo(sentTo);
      notification.setStatus(status);
    }

    notificationRepository.save(notification);
  }

  @Transactional
  public void saveInAppNotification(
      String id, String title, String content, String notificationId) {
    InAppNotificationEntity entity = new InAppNotificationEntity();
    entity.setId(id);
    entity.setTitle(title);
    entity.setContent(content);
    entity.setIsRead(false);
    entity.setNotificationId(notificationId);

    inAppNotificationRepository.save(entity);
  }
}
