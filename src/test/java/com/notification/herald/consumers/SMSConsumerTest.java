package com.notification.herald.consumers;

import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.enums.NotifTypeEnum;
import com.notification.herald.enums.NotificationStatusEnum;
import com.notification.herald.enums.SMSProviderEnum;
import com.notification.herald.services.CommonPersistanceService;
import com.notification.herald.utils.SMSUtil;
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
class SMSConsumerTest {

    @Mock
    SMSUtil smsUtil;

    @Mock
    CommonPersistanceService commonPersistanceService;

    @InjectMocks
    SMSConsumer smsConsumer;

    private final SMSRequestDto request = new SMSRequestDto("+1234567890", "Hello", "req-xyz");

    @Test
    void smsConsumer_success_shouldCallSMSUtilAndPersistRequested() throws Exception {
        when(smsUtil.sendSMS(request, SMSProviderEnum.TWILIO)).thenReturn("sms-sid-123");

        smsConsumer.smsConsumer(request, 1);

        verify(smsUtil).sendSMS(request, SMSProviderEnum.TWILIO);
        verify(commonPersistanceService).saveOrUpdateNotification(
                eq("req-xyz"), eq("sms-sid-123"), eq(0), eq(NotifTypeEnum.SMS), eq(NotificationStatusEnum.REQUESTED)
        );
    }

    @Test
    void smsConsumer_success_deliveryAttemptMinusOne_passedToPersistence() throws Exception {
        when(smsUtil.sendSMS(any(), any())).thenReturn("sid");

        smsConsumer.smsConsumer(request, 2);

        verify(commonPersistanceService).saveOrUpdateNotification(
                any(), any(), eq(1), any(), any()
        );
    }

    @Test
    void smsConsumer_failure_shouldPersistFailedAndRethrow() throws Exception {
        RuntimeException cause = new RuntimeException("sms error");
        when(smsUtil.sendSMS(any(), any())).thenThrow(cause);

        assertThatThrownBy(() -> smsConsumer.smsConsumer(request, 1))
                .isSameAs(cause);

        verify(commonPersistanceService).saveOrUpdateNotification(
                eq("req-xyz"), eq("FAILED_REFERENCE"), eq(0), eq(NotifTypeEnum.SMS), eq(NotificationStatusEnum.FAILED)
        );
    }

    @Test
    void smsConsumer_failure_shouldNotPersistRequested() throws Exception {
        when(smsUtil.sendSMS(any(), any())).thenThrow(new RuntimeException("error"));

        try {
            smsConsumer.smsConsumer(request, 1);
        } catch (Exception ignored) {}

        verify(commonPersistanceService, never()).saveOrUpdateNotification(
                any(), any(), any(), any(), eq(NotificationStatusEnum.REQUESTED)
        );
    }
}
