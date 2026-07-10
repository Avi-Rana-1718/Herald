package com.notification.herald.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.notification.herald.dto.EmailNotifRequestDto;
import com.notification.herald.dto.ResponseDto;
import com.notification.herald.dto.mail.MailRequestDto;
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
class EmailNotificationServiceTest {

  @Mock private KafkaProviderService kafkaProviderService;

  @InjectMocks private EmailNotificationService emailNotificationService;

  @Test
  void sendEmail_shouldPublishToEmailTopic() {
    EmailNotifRequestDto request =
        new EmailNotifRequestDto("user@example.com", "Alice", "Subject", "Hello email");

    emailNotificationService.sendEmail(request);

    ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaProviderService).sendMessage(eq("EMAIL"), messageCaptor.capture());
    assertThat(messageCaptor.getValue()).isInstanceOf(MailRequestDto.class);

    MailRequestDto mail = (MailRequestDto) messageCaptor.getValue();
    assertThat(mail.to().email()).isEqualTo("user@example.com");
    assertThat(mail.subject()).isEqualTo("Subject");
    assertThat(mail.content()).isEqualTo("Hello email");
  }

  @Test
  void sendEmail_shouldReturnGeneratedRequestId() {
    EmailNotifRequestDto request = new EmailNotifRequestDto("a@b.com", "Name", "subj", "content");

    ResponseDto response = emailNotificationService.sendEmail(request);

    assertThat(response.status()).isEqualTo(HttpStatus.CREATED.value());
    List<String> ids = (List<String>) response.data();
    assertThat(ids).hasSize(1);

    ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
    verify(kafkaProviderService).sendMessage(any(), captor.capture());
    MailRequestDto mail = (MailRequestDto) captor.getValue();
    assertThat(mail.requestId()).isEqualTo(ids.get(0));
  }

  @Test
  void sendEmail_nullEmail_shouldThrow400() {
    EmailNotifRequestDto request = new EmailNotifRequestDto(null, "Name", "subj", "content");

    assertThatThrownBy(() -> emailNotificationService.sendEmail(request))
        .isInstanceOf(ResponseStatusException.class)
        .satisfies(
            ex ->
                assertThat(((ResponseStatusException) ex).getStatusCode())
                    .isEqualTo(HttpStatus.BAD_REQUEST));
  }
}
