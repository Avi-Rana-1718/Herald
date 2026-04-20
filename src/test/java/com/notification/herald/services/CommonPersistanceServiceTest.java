package com.notification.herald.services;

import com.notification.herald.entities.NotificationEntity;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonPersistanceServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private CommonPersistanceService commonPersistanceService;

    @Test
    void saveOrUpdateNotification_whenNoExistingRecord_shouldCreateNewEntity() {
        when(notificationRepository.findByID("req-1")).thenReturn(null);

        commonPersistanceService.saveOrUpdateNotification("req-1", "ref-abc", 1, NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED);

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getNotificationId()).isEqualTo("req-1");
        assertThat(saved.getReferenceId()).isEqualTo("ref-abc");
        assertThat(saved.getType()).isEqualTo(NotifTypeEnum.EMAIL);
        assertThat(saved.getStatus()).isEqualTo(NotificationStatusEnum.REQUESTED);
        assertThat(saved.getRetryCount()).isEqualTo(1);
    }

    @Test
    void saveOrUpdateNotification_whenExistingRecord_shouldUpdateFields() {
        NotificationEntity existing = new NotificationEntity("req-1", "old-ref", NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 1);
        when(notificationRepository.findByID("req-1")).thenReturn(existing);

        commonPersistanceService.saveOrUpdateNotification("req-1", "new-ref", 2, NotifTypeEnum.EMAIL, NotificationStatusEnum.FAILED);

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        NotificationEntity saved = captor.getValue();
        assertThat(saved.getReferenceId()).isEqualTo("new-ref");
        assertThat(saved.getRetryCount()).isEqualTo(2);
        assertThat(saved.getStatus()).isEqualTo(NotificationStatusEnum.FAILED);
    }

    @Test
    void saveOrUpdateNotification_whenExistingRecord_shouldNotCreateNewInstance() {
        NotificationEntity existing = new NotificationEntity("req-1", "old-ref", NotifTypeEnum.SMS, NotificationStatusEnum.REQUESTED, 1);
        when(notificationRepository.findByID("req-1")).thenReturn(existing);

        commonPersistanceService.saveOrUpdateNotification("req-1", "new-ref", 2, NotifTypeEnum.SMS, NotificationStatusEnum.REQUESTED);

        ArgumentCaptor<NotificationEntity> captor = ArgumentCaptor.forClass(NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());

        // Should be the same instance, not a new one
        assertThat(captor.getValue()).isSameAs(existing);
    }

    @Test
    void saveOrUpdateNotification_shouldAlwaysCallSave() {
        when(notificationRepository.findByID(any())).thenReturn(null);

        commonPersistanceService.saveOrUpdateNotification("req-1", "ref-1", 1, NotifTypeEnum.SMS, NotificationStatusEnum.REQUESTED);

        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }
}
