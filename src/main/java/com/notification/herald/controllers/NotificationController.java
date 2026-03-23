package com.notification.herald.controllers;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @PostMapping
    public ResponseEntity<ResponseDto> triggerNotification(@Valid @RequestBody List<NotifRequestDto> requestDto)
    {
        ResponseDto responseDto = notificationService.sendNotification(requestDto);
        return ResponseEntity.status(responseDto.status()).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getNotification(@RequestParam("requestId") String requestId) {
        ResponseDto responseDto = notificationService.getNotification(requestId);
        return ResponseEntity.status(responseDto.status()).body(responseDto);
    }

}
