package com.notification.herald.entities;

import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {

    @Id
    @Column(name ="notification_id",unique = true)
    String notificationId;

    @Column(name = "reference_id", unique = true)
    String referenceId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationType")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    NotifTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "notificationStatus")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    NotificationStatusEnum status;

    @Column(name = "retry_count")
    Integer retryCount;
}
