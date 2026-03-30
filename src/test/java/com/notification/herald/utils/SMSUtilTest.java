package com.notification.herald.utils;

import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.enums.SMSProviderEnum;
import com.notification.herald.providers.sms.SMSProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SMSUtilTest {

    @Test
    void sendSMS_withValidProvider_shouldDelegateToProvider() {
        TwilioImpl provider = spy(new TwilioImpl());
        SMSUtil smsUtil = new SMSUtil(List.of(provider));
        SMSRequestDto request = new SMSRequestDto("+1234567890", "Hello", "req-1");

        String result = smsUtil.sendSMS(request, SMSProviderEnum.TWILIO);

        verify(provider).sendSMS(request);
        assertThat(result).isEqualTo("test-sid");
    }

    @Test
    void sendSMS_withUnknownProvider_shouldThrowNullPointerException() {
        // SMSUtil has no null check — calling sendSMS with a provider not in the map NPEs
        SMSUtil smsUtil = new SMSUtil(List.of());
        SMSRequestDto request = new SMSRequestDto("+1234567890", "Hello", "req-1");

        assertThatThrownBy(() -> smsUtil.sendSMS(request, SMSProviderEnum.TWILIO))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void constructor_shouldRegisterProviderByStrippedClassName() {
        SMSUtil smsUtil = new SMSUtil(List.of(new TwilioImpl()));
        SMSRequestDto request = new SMSRequestDto("+1234567890", "msg", "req-1");

        String result = smsUtil.sendSMS(request, SMSProviderEnum.TWILIO);
        assertThat(result).isEqualTo("test-sid");
    }

    // Inner class whose simple name "TwilioImpl" maps to key "twilio"
    static class TwilioImpl implements SMSProvider {
        @Override
        public String sendSMS(SMSRequestDto request) {
            return "test-sid";
        }
    }
}
