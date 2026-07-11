package com.notification.herald.services;

import com.notification.herald.dto.InAppNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.inapp.InAppRequestDto;
import com.notification.herald.entities.InAppNotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.repository.InAppNotificationRepository;
import com.notification.herald.utils.RequestUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InAppNotificationService {

  private final KafkaProviderService kafkaProviderService;
  private final InAppNotificationRepository inAppNotificationRepository;

  public ResponseDto sendInApp(InAppNotifRequestDto request) {
    String requestId = RequestUtils.generateRequestId();
    InAppRequestDto inAppRequestDto =
        new InAppRequestDto(request.getUuid(), request.getTitle(), request.getContent(), requestId);

    kafkaProviderService.sendMessage(NotifTypeEnum.IN_APP.toString(), inAppRequestDto);

    return new ResponseDto(List.of(requestId), HttpStatus.CREATED.value());
  }

  @Transactional
  public ResponseDto getInbox(String uuid) {
    List<InAppNotificationEntity> unread = inAppNotificationRepository.findUnreadInboxByUuid(uuid);

    unread.forEach(entity -> entity.setIsRead(true));
    inAppNotificationRepository.saveAll(unread);

    return new ResponseDto(unread, HttpStatus.OK.value());
  }
}
