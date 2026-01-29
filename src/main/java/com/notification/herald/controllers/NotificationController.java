package com.notification.herald.controllers;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private NotificationService notificationService;
    NotificationController(final NotificationService notificationService){
        this.notificationService = notificationService; 
    }


    @PostMapping
    public ResponseEntity<ResponseDto> triggerNotification(@Valid @RequestBody NotifRequestDto requestDto)
    {
        return ResponseEntity.status(200).body(notificationService.sendNotification(requestDto));
    }

}
