package com.notification.herald.utils;

import com.notification.herald.dto.UserDto;
import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.providers.mail.MailProvider;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class MailUtilTest {

    // Class name "MailjetImpl" → lowercase → "mailjetimpl" → strip "impl" → "mailjet"
    // Matches MailProviderEnum.MAILJET.getValue() = "mailjet"
    static class MailjetImpl implements MailProvider {
        @Override
        public String sendMail(MailRequestDto request) {
            return "test-message-id";
        }
    }

    private MailRequestDto sampleRequest() {
        return new MailRequestDto("Subject", "Body", new UserDto("Alice", "user@example.com"), "req-1");
    }

    @Test
    void sendMail_withValidProvider_shouldDelegateToProvider() throws Exception {
        MailjetImpl provider = spy(new MailjetImpl());
        MailUtil mailUtil = new MailUtil(List.of(provider));

        String result = mailUtil.sendMail(sampleRequest(), MailProviderEnum.MAILJET);

        verify(provider).sendMail(sampleRequest());
        assertThat(result).isEqualTo("test-message-id");
    }

    @Test
    void sendMail_withUnknownProvider_shouldThrowBadRequestException() {
        MailUtil mailUtil = new MailUtil(List.of());

        assertThatThrownBy(() -> mailUtil.sendMail(sampleRequest(), MailProviderEnum.MAILJET))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid email provider");
    }

    @Test
    void constructor_shouldRegisterProviderWithStrippedImplKey() throws Exception {
        MailUtil mailUtil = new MailUtil(List.of(new MailjetImpl()));

        String result = mailUtil.sendMail(sampleRequest(), MailProviderEnum.MAILJET);

        assertThat(result).isEqualTo("test-message-id");
    }
}
