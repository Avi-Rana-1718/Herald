package com.notification.herald.services;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService {


    private KafkaProviderService kafkaProviderService;

    NotificationService(KafkaProviderService kafkaProviderService) {
        this.kafkaProviderService = kafkaProviderService;
    }

    public ResponseEntity<ResponseDto> triggerEmail(MailRequestDto request) {
      UUID requestId = UUID.randomUUID();
      ResponseDto response = new ResponseDto(requestId, HttpStatus.CREATED.value());

      kafkaProviderService.sendMessage("emails", request);

      return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.status()));
    }
}
