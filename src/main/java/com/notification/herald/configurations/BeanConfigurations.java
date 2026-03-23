package com.notification.herald.configurations;

import com.notification.herald.configurations.properties.MailjetConfiguration;
import com.notification.herald.configurations.properties.TwilioConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class BeanConfigurations {

  private final MailjetConfiguration mailjetConfiguration;
  private final TwilioConfiguration twilioConfiguration;

  @Bean
  public RestClient mailjetClient(RestClient.Builder builder) {
    return builder.baseUrl(mailjetConfiguration.baseUrl()).defaultHeaders(headers->{
      headers.setBasicAuth(mailjetConfiguration.username(), mailjetConfiguration.password());
      headers.setContentType(MediaType.APPLICATION_JSON);
    }).build();
  }

  @Bean
    public RestClient twilioClient(RestClient.Builder builder) {
      return builder.baseUrl(twilioConfiguration.baseUrl()).defaultHeaders(httpHeaders -> {
          httpHeaders.setBasicAuth(twilioConfiguration.username(), twilioConfiguration.password());
          httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
      }).build();
  }
}
