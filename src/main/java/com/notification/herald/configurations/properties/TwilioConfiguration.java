package com.notification.herald.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sms.twilio")
public record TwilioConfiguration(
        String username,
        String password,
        String serviceId,
        String baseUrl
) {}
