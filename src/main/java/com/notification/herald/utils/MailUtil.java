package com.notification.herald.utils;

import com.notification.herald.dto.mail.MailRequestDto;
import com.notification.herald.enums.MailProviderEnum;
import com.notification.herald.providers.mail.MailProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MailUtil {

    private HashMap<String, MailProvider> providerList;

    MailUtil(List<MailProvider> providerList) {
        this.providerList = (HashMap<String, MailProvider>) providerList.stream().collect(Collectors.toMap(provider->provider.getClass().getSimpleName().toLowerCase().replace("impl", ""), provider ->provider));
    }

    public String sendMail(MailRequestDto request, MailProviderEnum provider) throws Exception {
        String providerName = provider.getValue().toLowerCase();
        MailProvider mailProvider = providerList.get(providerName);

        if(Objects.isNull(mailProvider)) {
            throw new Exception("Invalid mail provider!");
        }

        return mailProvider.sendMail(request).block();
    }
}
