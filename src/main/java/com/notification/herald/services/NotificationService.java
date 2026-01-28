package com.notification.herald.services;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotificationTypeEnum;
import com.notification.herald.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {


    private KafkaProviderService kafkaProviderService;
    private NotificationRepository notificationRepository;

    NotificationService(KafkaProviderService kafkaProviderService, NotificationRepository notificationRepository) {
        this.kafkaProviderService = kafkaProviderService;
        this.notificationRepository = notificationRepository;
    }

    public ResponseEntity<ResponseDto> triggerEmail(MailRequestDto request) {
      UUID requestId = UUID.randomUUID();
      ResponseDto response = new ResponseDto(requestId, HttpStatus.CREATED.value());
      EventDto eventDto = new EventDto(requestId, requestId, NotificationTypeEnum.EMAIL.toString(), request);
      kafkaProviderService.sendMessage("emails", eventDto);

      return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.status()));
    }

    public ResponseEntity<ResponseDto> getEmailStatus(String requestId) {
        UUID reqId  = UUID.fromString(requestId);
        Optional<NotificationEntity> entity = notificationRepository.findById(reqId);
        if(entity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request ID not found");
        }

        ResponseDto response = new ResponseDto(entity, HttpStatus.OK.value());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.status()));
    }
}
