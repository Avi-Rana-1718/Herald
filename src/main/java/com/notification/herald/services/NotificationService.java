package com.notification.herald.services;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public ResponseDto getNotification(String requestId) {
    NotificationEntity notification = notificationRepository.findByID(requestId);

    return new ResponseDto(notification, HttpStatus.OK.value());
  }
}
