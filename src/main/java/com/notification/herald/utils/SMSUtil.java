package com.notification.herald.utils;

import com.notification.herald.dto.sms.SMSRequestDto;
import com.notification.herald.enums.SMSProviderEnum;
import com.notification.herald.providers.sms.SMSProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SMSUtil {
    HashMap<String, SMSProvider> providerList;

    SMSUtil(List<SMSProvider> providerList) {
        this.providerList = (HashMap<String, SMSProvider>) providerList.stream().collect(Collectors.toMap(provider->provider.getClass().getSimpleName().toLowerCase().replace("impl", ""), provider ->provider));
    }

    public String sendSMS(SMSRequestDto request, SMSProviderEnum provider) {
        String providerName = provider.getValue().toLowerCase();
        SMSProvider smsProvider = providerList.get(providerName);
        return smsProvider.sendSMS(request);
    }
}
