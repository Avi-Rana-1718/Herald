package com.notification.herald.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.SMSNotifRequestDto;
import com.notification.herald.dto.sms.SMSRequestDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SMSNotificationServiceTest {

  @Mock private KafkaProviderService kafkaProviderService;

  @InjectMocks private SMSNotificationService smsNotificationService;

  @Test
  void sendSms_shouldPublishToSmsTopic() {
    SMSNotifRequestDto request = new SMSNotifRequestDto("+1234567890", "Hello");

    smsNotificationService.sendSms(request);

    ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaProviderService).sendMessage(eq("SMS"), messageCaptor.capture());
    assertThat(messageCaptor.getValue()).isInstanceOf(SMSRequestDto.class);

    SMSRequestDto sms = (SMSRequestDto) messageCaptor.getValue();
    assertThat(sms.toMobile()).isEqualTo("+1234567890");
    assertThat(sms.body()).isEqualTo("Hello");
  }

  @Test
  void sendSms_shouldReturnGeneratedRequestId() {
    SMSNotifRequestDto request = new SMSNotifRequestDto("+111", "msg");

    ResponseDto response = smsNotificationService.sendSms(request);

    assertThat(response.status()).isEqualTo(HttpStatus.CREATED.value());
    List<String> ids = (List<String>) response.data();
    assertThat(ids).hasSize(1);

    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaProviderService).sendMessage(any(), captor.capture());
    SMSRequestDto sms = (SMSRequestDto) captor.getValue();
    assertThat(sms.requestId()).isEqualTo(ids.get(0));
  }

  @Test
  void sendSms_nullMobile_shouldThrow400() {
    SMSNotifRequestDto request = new SMSNotifRequestDto(null, "msg");

    assertThatThrownBy(() -> smsNotificationService.sendSms(request))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex ->
                assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
  }
}
