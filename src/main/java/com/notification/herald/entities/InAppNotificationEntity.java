package com.notification.herald.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(name = "in_app_notifications")
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotificationEntity {

  @Id
  @Column(name = "id")
  String id;

  @Column(name = "title", nullable = false)
  String title;

  @Column(name = "content", nullable = false)
  String content;

  @Column(name = "is_read", nullable = false)
  Boolean isRead;

  @Column(name = "notification_id")
  String notificationId;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  LocalDateTime createdAt;
}
