package com.notification.herald.controllers;

import com.notification.herald.dto.EmailTemplateCreationDto;
import com.notification.herald.dto.EmailTemplateTriggerDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSTemplateCreationDto;
import com.notification.herald.dto.SMSTemplateTriggerDto;
import com.notification.herald.services.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController {

  private final TemplateService templateService;

  @PostMapping("/create/email")
  public ResponseEntity<ResponseDto> createEmailTemplate(
      @Valid @RequestBody EmailTemplateCreationDto templateCreationDto) {
    ResponseDto response = templateService.createTemplate(templateCreationDto);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/create/sms")
  public ResponseEntity<ResponseDto> createSMSTemplate(
      @Valid @RequestBody SMSTemplateCreationDto templateCreationDto) {
    ResponseDto response = templateService.createTemplate(templateCreationDto);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/trigger/email")
  public ResponseEntity<ResponseDto> triggerEmailTemplate(
      @Valid @RequestBody EmailTemplateTriggerDto triggerDto) {
    ResponseDto response = templateService.triggerEmailTemplate(triggerDto);
    return ResponseEntity.status(response.status()).body(response);
  }

  @PostMapping("/trigger/sms")
  public ResponseEntity<ResponseDto> triggerSMSTemplate(
      @Valid @RequestBody SMSTemplateTriggerDto triggerDto) {
    ResponseDto response = templateService.triggerSmsTemplate(triggerDto);
    return ResponseEntity.status(response.status()).body(response);
  }
}
