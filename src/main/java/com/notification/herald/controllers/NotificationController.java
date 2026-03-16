package com.notification.herald.controllers;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private NotificationService notificationService;


    @PostMapping
    public ResponseEntity<ResponseDto> triggerNotification(@Valid @RequestBody NotifRequestDto requestDto)
    {
        ResponseDto responseDto = notificationService.sendNotification(requestDto);
        return ResponseEntity.status(responseDto.status()).body(responseDto);
    }

}
