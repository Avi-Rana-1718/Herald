package com.notification.herald.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.notification.herald.dto.notification.TriggerNotificationDto;
import com.notification.herald.dto.notification.TriggerNotificationResponse;
import com.notification.herald.services.NotificationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/notification")
public class NotificationController {

    private NotificationService notificationService;
    NotificationController(final NotificationService notificationService){
        this.notificationService = notificationService; 
    }
    
    @PostMapping("trigger")
    public TriggerNotificationResponse postMethodName(@RequestBody TriggerNotificationDto request) {
        return this.notificationService.triggerNotification(request);
    }
    
}
