package com.notification.herald.services;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {


    private final KafkaProviderService kafkaProviderService;

  public ResponseDto sendNotification(NotifRequestDto notifRequestDto) {

      String requestId  = RequestUtils.generateRequestId();
      // iterate through list of providers and send request to kafka topics
      for(NotifTypeEnum type : notifRequestDto.type()) {
        UUID uid = UUID.randomUUID();
        EventDto eventDto = new EventDto(requestId,uid, notifRequestDto.recipients(),notifRequestDto.content(),notifRequestDto.subject());
        kafkaProviderService.sendMessage(type.toString(),eventDto);
      }

      return new ResponseDto(requestId, HttpStatus.CREATED.value());
    }
}
