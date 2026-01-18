package com.notification.herald.entities;

import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @Column(name ="notification_id",unique = true)
    UUID notificationId;

    @Column(name = "reference_id", unique = true)
    String referenceId;

    @Column(name = "user_id")
    UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationType")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    NotificationTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationStatus")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    NotificationStatusEnum status;

    @Column(name = "retry_count")
    Integer retryCount;
}
