package com.notification.herald.providers.inapp;

import com.notification.herald.dto.inapp.InAppRequestDto;

public interface InAppProvider {

  String sendNotification(InAppRequestDto request);
}
