package com.notification.herald.controllers;

import com.notification.herald.dto.InAppNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.services.InAppNotificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification/inapp")
@RequiredArgsConstructor
public class InAppController {

  private final InAppNotificationService inAppNotificationService;

  @PostMapping
  public ResponseEntity<ResponseDto> triggerInApp(
      @Valid @RequestBody InAppNotifRequestDto requestDto) {
    ResponseDto responseDto = inAppNotificationService.sendInApp(requestDto);
    return ResponseEntity.status(responseDto.status()).body(responseDto);
  }

  @GetMapping
  public ResponseEntity<ResponseDto> getInbox(@Valid @NotBlank @RequestParam("uuid") String uuid) {
    ResponseDto responseDto = inAppNotificationService.getInbox(uuid);
    return ResponseEntity.status(responseDto.status()).body(responseDto);
  }
}
