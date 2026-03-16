package com.notification.herald.providers.sms;

import com.notification.herald.configurations.properties.TwilioConfiguration;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.dto.sms.Twilio.TwilioRequestDto;
import com.notification.herald.dto.sms.Twilio.TwilioResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class TwilioImpl implements SMSProvider {

    private final RestClient smsClient;
    private final TwilioConfiguration twilioConfiguration;

    @Override
    public String sendSMS(SMSRequestDto request) {
        TwilioRequestDto requestDto = this.transform(request);

        TwilioResponseDto response = smsClient.post().body(requestDto).retrieve().body(TwilioResponseDto.class);
        return response.sid();
    }

    private TwilioRequestDto transform(SMSRequestDto request) {
        String serviceId = twilioConfiguration.serviceId();
        return new TwilioRequestDto(request.toMobile(), serviceId, request.body());
    }
}
