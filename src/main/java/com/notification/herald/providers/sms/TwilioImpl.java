package com.notification.herald.providers.sms;

import com.notification.herald.configurations.properties.TwilioConfiguration;
import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.dto.sms.Twilio.TwilioRequestDto;
import com.notification.herald.dto.sms.Twilio.TwilioResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class TwilioImpl implements SMSProvider {

    private final RestClient smsClient;
    private final TwilioConfiguration twilioConfiguration;

    public TwilioImpl(@Qualifier("twilioClient") RestClient smsClient,
                      TwilioConfiguration twilioConfiguration) {
        this.smsClient = smsClient;
        this.twilioConfiguration = twilioConfiguration;
    }

    @Override
    public String sendSMS(SMSRequestDto request) {
        TwilioRequestDto requestDto = this.transform(request);

        TwilioResponseDto response = smsClient.post().body(toFormData(requestDto)).retrieve().body(TwilioResponseDto.class);
        return response.sid();
    }

    private TwilioRequestDto transform(SMSRequestDto request) {
        String serviceId = twilioConfiguration.serviceId();
        return new TwilioRequestDto(request.toMobile(), serviceId, request.body());
    }

    private MultiValueMap<String, String> toFormData(TwilioRequestDto dto) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("To", dto.To());
        map.add("MessagingServiceSid", dto.MessagingServiceSid());
        map.add("Body", dto.Body());
        return map;
    }
}
