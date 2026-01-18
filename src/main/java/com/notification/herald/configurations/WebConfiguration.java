package com.notification.herald.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfiguration {

    private String mailjetUsername = "97fcfb69034b8f6aced283f42863ff1d";
    private String mailjetPassword = "6c15f1686003d3463b939bc237bb0ada";

    @Bean
   public WebClient MailClient(WebClient.Builder builder) {
       return builder.baseUrl("https://api.mailjet.com/v3.1/").defaultHeaders(headers->{
           headers.setBasicAuth(mailjetUsername, mailjetPassword);
           headers.setContentType(MediaType.APPLICATION_JSON);
       }).build();
   }
}
