package com.notification.herald.configurations.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail.mailjet")
public record MailjetConfiguration(

    String username,
    String password,
    String baseUrl,
    String email,
    String name
) {

}
