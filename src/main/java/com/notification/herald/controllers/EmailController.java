package com.notification.herald.controllers;

import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.EmailNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification/email")
@RequiredArgsConstructor
public class EmailController {

  private final EmailNotificationService emailNotificationService;

  @PostMapping
  public ResponseEntity<ResponseDto> triggerEmail(
      @Valid @RequestBody EmailNotifRequestDto requestDto) {
    ResponseDto responseDto = emailNotificationService.sendEmail(requestDto);
    return ResponseEntity.status(responseDto.status()).body(responseDto);
  }
}
