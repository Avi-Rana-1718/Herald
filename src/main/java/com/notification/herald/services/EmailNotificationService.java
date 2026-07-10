package com.notification.herald.services;

import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.UserDto;
import com.notification.herald.dto.mail.MailRequestDto;
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
public class EmailNotificationService {

  private final KafkaProviderService kafkaProviderService;

  public ResponseDto sendEmail(EmailNotifRequestDto request) {
    this.validateRequest(request);

    String requestId = RequestUtils.generateRequestId();
    UserDto userDto = new UserDto(request.getToName(), request.getToEmail());
    MailRequestDto mailRequestDto =
        new MailRequestDto(request.getSubject(), request.getContent(), userDto, requestId);

    kafkaProviderService.sendMessage(NotifTypeEnum.EMAIL.toString(), mailRequestDto);

    return new ResponseDto(List.of(requestId), HttpStatus.CREATED.value());
  }

  private void validateRequest(EmailNotifRequestDto request) {
    if (Objects.isNull(request.getToEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toEmail can't be null for email");
    }
  }
}
