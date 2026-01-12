package com.notification.herald.dto.notification;

import com.notification.herald.enums.NotificationTypeEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public record TriggerNotificationDto (
    @NotBlank()
    NotificationTypeEnum[] type,
    @Min(5)
    String contactAddress
) {}
