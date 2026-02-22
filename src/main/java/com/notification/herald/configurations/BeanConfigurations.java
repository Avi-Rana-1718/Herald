package com.notification.herald.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfigurations {

  private final MailjetConfiguration mailjetConfiguration;

  @Bean
  public RestClient mailClient(RestClient.Builder builder) {
    return builder.baseUrl(mailjetConfiguration.mailjetBaseURL()).defaultHeaders(headers->{
      headers.setBasicAuth(mailjetConfiguration.mailjetUsername(), mailjetConfiguration.mailjetPassword());
      headers.setContentType(MediaType.APPLICATION_JSON);
    }).build();
  }
}
