package com.notification.herald.providers.sms;

import com.notification.herald.dto.sms.SMSRequestDto;

public interface SMSProvider {
    String sendSMS(SMSRequestDto smsRequestDto);
}
