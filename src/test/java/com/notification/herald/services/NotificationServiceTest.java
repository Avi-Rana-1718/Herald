package com.notification.herald.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationService notificationService;

  @Test
  void getNotification_shouldReturnWrappedEntity() {
    NotificationEntity entity =
        new NotificationEntity(
            "req-1", "ref-1", NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 1);
    when(notificationRepository.findByID("req-1")).thenReturn(entity);

    ResponseDto response = notificationService.getNotification("req-1");

    assertThat(response.data()).isEqualTo(entity);
    assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
  }
}
