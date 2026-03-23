package com.notification.herald.repository;

import com.notification.herald.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {

    @NativeQuery("SELECT * FROM notifications WHERE notification_id = :requestId")
    NotificationEntity findByID(String requestId);

}
