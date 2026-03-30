package com.notification.herald.consumers;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.dto.UserDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.services.CommonPersistanceService;
import com.notification.herald.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConsumerTest {

    @Mock
    MailUtil mailUtil;

    @Mock
    CommonPersistanceService commonPersistanceService;

    @InjectMocks
    EmailConsumer emailConsumer;

    private final MailRequestDto request = new MailRequestDto(
            "Subject", "Body", new UserDto("Alice", "alice@example.com"), "req-abc"
    );

    @Test
    void emailConsumer_success_shouldCallMailUtilAndPersistRequested() throws Exception {
        when(mailUtil.sendMail(request, MailProviderEnum.MAILJET)).thenReturn("msg-ref-123");

        emailConsumer.emailConsumer(request, 1);

        verify(mailUtil).sendMail(request, MailProviderEnum.MAILJET);
        verify(commonPersistanceService).saveOrUpdateNotification(
                eq("req-abc"), eq("msg-ref-123"), eq(0), eq(NotifTypeEnum.EMAIL), eq(NotificationStatusEnum.REQUESTED)
        );
    }

    @Test
    void emailConsumer_success_deliveryAttemptMinusOne_passedToPersistence() throws Exception {
        when(mailUtil.sendMail(any(), any())).thenReturn("ref-id");

        emailConsumer.emailConsumer(request, 3);

        verify(commonPersistanceService).saveOrUpdateNotification(
                any(), any(), eq(2), any(), any()
        );
    }

    @Test
    void emailConsumer_failure_shouldPersistFailedAndRethrow() throws Exception {
        RuntimeException cause = new RuntimeException("mail error");
        when(mailUtil.sendMail(any(), any())).thenThrow(cause);

        assertThatThrownBy(() -> emailConsumer.emailConsumer(request, 1))
                .isSameAs(cause);

        verify(commonPersistanceService).saveOrUpdateNotification(
                eq("req-abc"), eq("FAILED_REFERENCE"), eq(0), eq(NotifTypeEnum.EMAIL), eq(NotificationStatusEnum.FAILED)
        );
    }

    @Test
    void emailConsumer_failure_shouldNotPersistRequested() throws Exception {
        when(mailUtil.sendMail(any(), any())).thenThrow(new RuntimeException("error"));

        try {
            emailConsumer.emailConsumer(request, 1);
        } catch (Exception ignored) {}

        verify(commonPersistanceService, never()).saveOrUpdateNotification(
                any(), any(), any(), any(), eq(NotificationStatusEnum.REQUESTED)
        );
    }
}
