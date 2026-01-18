package com.notification.herald.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfiguration {

    @Value("${mail.mailjet.username}")
    private String mailjetUsername;
    @Value("${mail.mailjet.password}")
    private String mailjetPassword;
    @Value("${mail.mailjet.url}")
    private String mailjetBaseURL;

    @Bean
   public WebClient mailClient(WebClient.Builder builder) {
       return builder.baseUrl(mailjetBaseURL).defaultHeaders(headers->{
           headers.setBasicAuth(mailjetUsername, mailjetPassword);
           headers.setContentType(MediaType.APPLICATION_JSON);
       }).build();
   }
}
