package com.notification.herald.consumers;

import com.notification.herald.dto.inapp.InAppRequestDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.providers.inapp.InAppProvider;
import com.notification.herald.services.CommonPersistanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InAppConsumer {

  private final InAppProvider inAppProvider;
  private final CommonPersistanceService commonPersistanceService;

  private final String FAILED_REFERENCE = "FAILED_REFERENCE";

  @KafkaListener(topics = "IN_APP")
  public void inAppConsumer(
      InAppRequestDto request,
      @Header(value = KafkaHeaders.DELIVERY_ATTEMPT) Integer deliveryAttempt)
      throws Exception {
    String requestId = request.requestId();

    try {
      String referenceId = inAppProvider.sendNotification(request);
      commonPersistanceService.saveOrUpdateNotification(
          requestId,
          referenceId,
          request.uuid(),
          deliveryAttempt - 1,
          NotifTypeEnum.IN_APP,
          NotificationStatusEnum.REQUESTED);
    } catch (Exception e) {
      commonPersistanceService.saveOrUpdateNotification(
          requestId,
          FAILED_REFERENCE,
          request.uuid(),
          deliveryAttempt - 1,
          NotifTypeEnum.IN_APP,
          NotificationStatusEnum.FAILED);
      throw e;
    }
  }
}
