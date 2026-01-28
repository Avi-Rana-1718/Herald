package com.notification.herald.controllers;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/notification")
public class NotificationController {

    private NotificationService notificationService;
    NotificationController(final NotificationService notificationService){
        this.notificationService = notificationService; 
    }
    
    @PostMapping("trigger/email")
    public ResponseEntity<ResponseDto> triggerMail(@Valid @RequestBody MailRequestDto request) throws Exception {
        return this.notificationService.triggerEmail(request);
    }

    @GetMapping("status/email")
    public ResponseEntity<ResponseDto> getEmailStatus(@RequestParam("requestId") String requestId) {
        return this.notificationService.getEmailStatus(requestId);
    }
}
