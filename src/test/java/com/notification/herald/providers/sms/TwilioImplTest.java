package com.notification.herald.providers.sms;

import com.notification.herald.configurations.properties.TwilioConfiguration;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.dto.sms.Twilio.TwilioResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TwilioImplTest {

    @Mock
    private RestClient smsClient;

    // RETURNS_SELF handles .body() returning the same mock so .retrieve() can be chained
    @Mock(answer = Answers.RETURNS_SELF)
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private TwilioImpl twilioImpl;

    @BeforeEach
    void setUp() {
        TwilioConfiguration config = new TwilioConfiguration("user", "pass", "MG-service-sid-123", "https://api.twilio.com/");
        twilioImpl = new TwilioImpl(smsClient, config);

        when(smsClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    }

    private TwilioResponseDto responseWithSid(String sid) {
        return new TwilioResponseDto(null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, sid, null, null, null, null);
    }

    @Test
    void sendSMS_shouldReturnSid() {
        when(responseSpec.body(TwilioResponseDto.class)).thenReturn(responseWithSid("SM-sid-999"));

        String sid = twilioImpl.sendSMS(new SMSRequestDto("+1234567890", "Hello", "req-1"));

        assertThat(sid).isEqualTo("SM-sid-999");
    }

    @Test
    void sendSMS_shouldBuildFormDataWithCorrectFields() {
        when(responseSpec.body(TwilioResponseDto.class)).thenReturn(responseWithSid("SM-sid-001"));

        twilioImpl.sendSMS(new SMSRequestDto("+9876543210", "Test message", "req-2"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<MultiValueMap<String, String>> captor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(requestBodyUriSpec).body(captor.capture());

        MultiValueMap<String, String> formData = captor.getValue();
        assertThat(formData.getFirst("To")).isEqualTo("+9876543210");
        assertThat(formData.getFirst("MessagingServiceSid")).isEqualTo("MG-service-sid-123");
        assertThat(formData.getFirst("Body")).isEqualTo("Test message");
    }

    @Test
    void sendSMS_whenRestClientThrows_propagatesException() {
        when(responseSpec.body(TwilioResponseDto.class)).thenThrow(new RestClientException("HTTP 401 Unauthorized"));

        assertThatThrownBy(() -> twilioImpl.sendSMS(new SMSRequestDto("+1234567890", "Hello", "req-fail")))
                .isInstanceOf(RestClientException.class)
                .hasMessageContaining("HTTP 401 Unauthorized");
    }

    @Test
    void sendSMS_shouldUseServiceIdFromConfiguration() {
        when(responseSpec.body(TwilioResponseDto.class)).thenReturn(responseWithSid("SM-sid-001"));

        twilioImpl.sendSMS(new SMSRequestDto("+1111111111", "msg", "req-3"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<MultiValueMap<String, String>> captor = ArgumentCaptor.forClass(MultiValueMap.class);
        verify(requestBodyUriSpec).body(captor.capture());

        assertThat(captor.getValue().getFirst("MessagingServiceSid")).isEqualTo("MG-service-sid-123");
    }
}
