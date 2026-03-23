package com.notification.herald.services;

import com.notification.herald.dto.EventDto;
import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.UserDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.repository.NotificationRepository;
import com.notification.herald.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {


    private final KafkaProviderService kafkaProviderService;
    private final NotificationRepository notificationRepository;

  public ResponseDto sendNotification(List<NotifRequestDto> notifRequestDto) {

      List<String> requestList = new ArrayList<>();

      for(NotifRequestDto request: notifRequestDto) {
          String requestId = RequestUtils.generateRequestId();
          NotifTypeEnum type = request.getType();

          this.validateRequest(request);

          if(NotifTypeEnum.SMS.equals(type)) {
              SMSRequestDto smsRequestDto = new SMSRequestDto(request.getToMobile(), request.getContent(), requestId);

              kafkaProviderService.sendMessage(type.toString(), smsRequestDto);
          } else if (NotifTypeEnum.EMAIL.equals(type)) {
              UserDto userDto = new UserDto(request.getToName(), request.getToEmail());
              MailRequestDto mailRequestDto = new MailRequestDto(request.getSubject(),request.getContent(), userDto, requestId);

              kafkaProviderService.sendMessage(type.toString(), mailRequestDto);
          }

          requestList.add(requestId);
      }

      return new ResponseDto(requestList, HttpStatus.CREATED.value());
    }

    public ResponseDto getNotification(String requestId) {
        NotificationEntity notification = notificationRepository.findByID(requestId);

        return new ResponseDto(notification, HttpStatus.OK.value());
    }

    private void validateRequest(NotifRequestDto request) {
        if(Objects.isNull(request.getToEmail()) && Objects.isNull(request.getToMobile())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toMobile and toEmail both can't be null");


        if(NotifTypeEnum.SMS.equals(request.getType()) && Objects.isNull(request.getToMobile())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toMobile can't be null for SMS");
        }

        if(NotifTypeEnum.EMAIL.equals(request.getType()) && Objects.isNull(request.getToEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "toEmail can't be null for email");
        }
    }
}
