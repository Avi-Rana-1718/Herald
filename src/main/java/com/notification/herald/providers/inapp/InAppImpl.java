package com.notification.herald.providers.inapp;

import com.notification.herald.dto.inapp.InAppRequestDto;
import com.notification.herald.services.CommonPersistanceService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InAppImpl implements InAppProvider {

  private final CommonPersistanceService commonPersistanceService;

  @Override
  public String sendNotification(InAppRequestDto request) {
    String id = UUID.randomUUID().toString();

    commonPersistanceService.saveInAppNotification(
        id, request.title(), request.body(), request.requestId());

    return id;
  }
}
