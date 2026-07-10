package com.notification.herald.services;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.utils.RequestUtils;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SMSNotificationService {

  private final KafkaProviderService kafkaProviderService;

  public ResponseDto sendSms(SMSNotifRequestDto request) {
    this.validateRequest(request);

    String requestId = RequestUtils.generateRequestId();
    SMSRequestDto smsRequestDto =
        new SMSRequestDto(request.getToMobile(), request.getContent(), requestId);

    kafkaProviderService.sendMessage(NotifTypeEnum.SMS.toString(), smsRequestDto);

    return new ResponseDto(List.of(requestId), HttpStatus.CREATED.value());
  }

  private void validateRequest(SMSNotifRequestDto request) {
    if (Objects.isNull(request.getToMobile())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toMobile can't be null for SMS");
    }
  }
}
