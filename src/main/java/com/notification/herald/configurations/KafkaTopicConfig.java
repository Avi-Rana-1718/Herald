package com.notification.herald.configurations;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Value("${kafka.topics.partitions:2}")
  private int partitions;

  @Value("${kafka.topics.replicas:2}")
  private short replicas;

  @Bean
  public NewTopic emailTopic() {
    return TopicBuilder.name("EMAIL").partitions(partitions).replicas(replicas).build();
  }

  @Bean
  public NewTopic smsTopic() {
    return TopicBuilder.name("SMS").partitions(partitions).replicas(replicas).build();
  }

  @Bean
  public NewTopic inAppTopic() {
    return TopicBuilder.name("IN_APP").partitions(partitions).replicas(replicas).build();
  }
}
