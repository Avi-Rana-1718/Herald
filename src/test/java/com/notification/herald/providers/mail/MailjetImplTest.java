package com.notification.herald.providers.mail;

import com.notification.herald.configurations.properties.MailjetConfiguration;
import com.notification.herald.dto.UserDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.mail.Mailjet.request.MailjetRequestDto;
import com.notification.herald.dto.mail.Mailjet.response.MailjetMessages;
import com.notification.herald.dto.mail.Mailjet.response.MailjetResponseDto;
import com.notification.herald.dto.mail.Mailjet.response.MailjetTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;

import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailjetImplTest {

    @Mock
    private RestClient mailClient;

    // RETURNS_SELF handles the fluent chain: .uri().contentType().body() all return the same mock
    @Mock(answer = Answers.RETURNS_SELF)
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private MailjetImpl mailjetImpl;

    @BeforeEach
    void setUp() {
        MailjetConfiguration config = new MailjetConfiguration("apikey", "secret", "https://api.mailjet.com/v3.1/", "from@example.com", "Sender Name");
        mailjetImpl = new MailjetImpl(mailClient, config);

        when(mailClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    }

    private MailjetResponseDto responseWith(String messageId) {
        return new MailjetResponseDto(List.of(
                new MailjetMessages("success", List.of(new MailjetTo("user@example.com", messageId, "https://href")))
        ));
    }

    @Test
    void sendMail_shouldReturnMessageId() {
        when(responseSpec.body(MailjetResponseDto.class)).thenReturn(responseWith("msg-id-001"));

        String messageId = mailjetImpl.sendMail(new MailRequestDto("Subject", "Body content", new UserDto("Alice", "user@example.com"), "req-1"));

        assertThat(messageId).isEqualTo("msg-id-001");
    }

    @Test
    void sendMail_shouldPostToSendEndpoint() {
        when(responseSpec.body(MailjetResponseDto.class)).thenReturn(responseWith("msg-id-001"));

        mailjetImpl.sendMail(new MailRequestDto("Subject", "Body", new UserDto("Alice", "user@example.com"), "req-1"));

        verify(requestBodyUriSpec).uri("send");
    }

    @Test
    void sendMail_shouldTransformRecipientCorrectly() {
        when(responseSpec.body(MailjetResponseDto.class)).thenReturn(responseWith("msg-id-001"));

        mailjetImpl.sendMail(new MailRequestDto("Test Subject", "Test Body", new UserDto("Alice", "user@example.com"), "req-1"));

        ArgumentCaptor<MailjetRequestDto> captor = ArgumentCaptor.forClass(MailjetRequestDto.class);
        verify(requestBodyUriSpec).body(captor.capture());

        MailjetRequestDto sent = captor.getValue();
        assertThat(sent.Messages().get(0).To().get(0).email()).isEqualTo("user@example.com");
        assertThat(sent.Messages().get(0).To().get(0).name()).isEqualTo("Alice");
        assertThat(sent.Messages().get(0).Subject()).isEqualTo("Test Subject");
        assertThat(sent.Messages().get(0).HTMLPart()).isEqualTo("Test Body");
    }

    @Test
    void sendMail_whenRestClientThrows_propagatesException() {
        when(responseSpec.body(MailjetResponseDto.class)).thenThrow(new RestClientException("HTTP 400 Bad Request"));

        assertThatThrownBy(() -> mailjetImpl.sendMail(new MailRequestDto("Subject", "Body", new UserDto("Alice", "user@example.com"), "req-1")))
                .isInstanceOf(RestClientException.class)
                .hasMessageContaining("HTTP 400 Bad Request");
    }

    @Test
    void sendMail_shouldUseConfiguredSenderAddress() {
        when(responseSpec.body(MailjetResponseDto.class)).thenReturn(responseWith("msg-id-001"));

        mailjetImpl.sendMail(new MailRequestDto("Subject", "Body", new UserDto("Alice", "user@example.com"), "req-1"));

        ArgumentCaptor<MailjetRequestDto> captor = ArgumentCaptor.forClass(MailjetRequestDto.class);
        verify(requestBodyUriSpec).body(captor.capture());

        assertThat(captor.getValue().Messages().get(0).From().email()).isEqualTo("from@example.com");
        assertThat(captor.getValue().Messages().get(0).From().name()).isEqualTo("Sender Name");
    }
}
