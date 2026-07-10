package com.notification.herald.controllers;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.services.SMSNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification/sms")
@RequiredArgsConstructor
public class SMSController {

  private final SMSNotificationService smsNotificationService;

  @PostMapping
  public ResponseEntity<ResponseDto> triggerSms(@Valid @RequestBody SMSNotifRequestDto requestDto) {
    ResponseDto responseDto = smsNotificationService.sendSms(requestDto);
    return ResponseEntity.status(responseDto.status()).body(responseDto);
  }
}
