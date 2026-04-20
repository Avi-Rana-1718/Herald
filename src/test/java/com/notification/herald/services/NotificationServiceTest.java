package com.notification.herald.services;

import com.notification.herald.dto.NotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.sms.SMSRequestDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private KafkaProviderService kafkaProviderService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_smsRequest_shouldPublishToSmsTopic() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.SMS, "+1234567890", null, null, "Hello", null);

        ResponseDto response = notificationService.sendNotification(List.of(request));

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaProviderService).sendMessage(eq("SMS"), messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isInstanceOf(SMSRequestDto.class);

        SMSRequestDto sms = (SMSRequestDto) messageCaptor.getValue();
        assertThat(sms.toMobile()).isEqualTo("+1234567890");
        assertThat(sms.body()).isEqualTo("Hello");
    }

    @Test
    void sendNotification_emailRequest_shouldPublishToEmailTopic() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.EMAIL, null, "user@example.com", "Alice", "Hello email", "Subject");

        notificationService.sendNotification(List.of(request));

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaProviderService).sendMessage(eq("EMAIL"), messageCaptor.capture());
        assertThat(messageCaptor.getValue()).isInstanceOf(MailRequestDto.class);

        MailRequestDto mail = (MailRequestDto) messageCaptor.getValue();
        assertThat(mail.to().email()).isEqualTo("user@example.com");
        assertThat(mail.subject()).isEqualTo("Subject");
        assertThat(mail.content()).isEqualTo("Hello email");
    }

    @Test
    void sendNotification_shouldReturnListOfRequestIds() {
        NotifRequestDto r1 = new NotifRequestDto(NotifTypeEnum.SMS, "+111", null, null, "msg", null);
        NotifRequestDto r2 = new NotifRequestDto(NotifTypeEnum.EMAIL, null, "a@b.com", "Name", "content", "subj");

        ResponseDto response = notificationService.sendNotification(List.of(r1, r2));

        assertThat(response.status()).isEqualTo(HttpStatus.CREATED.value());
        List<String> ids = (List<String>) response.data();
        assertThat(ids).hasSize(2);
        assertThat(ids.get(0)).isNotEqualTo(ids.get(1));
    }

    @Test
    void sendNotification_shouldIncludeGeneratedRequestIdInKafkaMessage() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.SMS, "+1234567890", null, null, "msg", null);

        ResponseDto response = notificationService.sendNotification(List.of(request));
        String returnedId = ((List<String>) response.data()).get(0);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaProviderService).sendMessage(any(), captor.capture());
        SMSRequestDto sms = (SMSRequestDto) captor.getValue();

        assertThat(sms.requestId()).isEqualTo(returnedId);
    }

    @Test
    void validateRequest_bothMobileAndEmailNull_shouldThrow400() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.SMS, null, null, null, "msg", null);

        assertThatThrownBy(() -> notificationService.sendNotification(List.of(request)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void validateRequest_smsWithNullMobile_shouldThrow400() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.SMS, null, "user@example.com", null, "msg", null);

        assertThatThrownBy(() -> notificationService.sendNotification(List.of(request)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void validateRequest_emailWithNullEmail_shouldThrow400() {
        NotifRequestDto request = new NotifRequestDto(NotifTypeEnum.EMAIL, "+1234567890", null, "Name", "content", "subj");

        assertThatThrownBy(() -> notificationService.sendNotification(List.of(request)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void getNotification_shouldReturnWrappedEntity() {
        NotificationEntity entity = new NotificationEntity("req-1", "ref-1", NotifTypeEnum.EMAIL, NotificationStatusEnum.REQUESTED, 1);
        when(notificationRepository.findByID("req-1")).thenReturn(entity);

        ResponseDto response = notificationService.getNotification("req-1");

        assertThat(response.data()).isEqualTo(entity);
        assertThat(response.status()).isEqualTo(HttpStatus.OK.value());
    }
}
