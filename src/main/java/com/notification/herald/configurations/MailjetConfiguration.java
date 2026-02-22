package com.notification.herald.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail.mailjet")
public record MailjetConfiguration(

    String mailjetUsername,
    String mailjetPassword,
    String mailjetBaseURL,
    String email,
    String name
) {

}
