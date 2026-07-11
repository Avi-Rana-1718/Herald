package com.notification.herald.repository;

import com.notification.herald.entities.InAppNotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InAppNotificationRepository
    extends JpaRepository<InAppNotificationEntity, String> {

  @Query(
      "SELECT c FROM InAppNotificationEntity c, NotificationEntity n "
          + "WHERE c.notificationId = n.notificationId "
          + "AND n.sentTo = :uuid "
          + "AND n.type = com.notification.herald.enums.NotifTypeEnum.IN_APP "
          + "AND c.isRead = false "
          + "ORDER BY c.createdAt DESC")
  List<InAppNotificationEntity> findUnreadInboxByUuid(@Param("uuid") String uuid);
}
