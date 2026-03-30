package com.notification.herald.configurations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    @Mock
    ConsumerFactory<Object, Object> consumerFactory;

    private final KafkaConfig kafkaConfig = new KafkaConfig();

    @Test
    void errorHandler_shouldBeNonNull() {
        DefaultErrorHandler handler = kafkaConfig.errorHandler();

        assertThat(handler).isNotNull();
    }

    @Test
    void kafkaListenerContainerFactory_shouldEnableDeliveryAttemptHeader() {
        DefaultErrorHandler handler = kafkaConfig.errorHandler();

        ConcurrentKafkaListenerContainerFactory<?, ?> factory =
                kafkaConfig.kafkaListenerContainerFactory(consumerFactory, handler);

        assertThat(factory.getContainerProperties().isDeliveryAttemptHeader()).isTrue();
    }
}
