package com.domeni.kapita.config.kafka;

import cm.lao.generated.domeni.kapita.event.dto.UserCreatedEventEnvelopeDTO;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

  @Bean
  public ConsumerFactory<String, UserCreatedEventEnvelopeDTO> userCreatedEventConsumerFactory(
      KafkaProperties kafkaProperties) {
    Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

    JsonDeserializer<UserCreatedEventEnvelopeDTO> valueDeserializer =
        new JsonDeserializer<>(UserCreatedEventEnvelopeDTO.class, false);
    valueDeserializer.addTrustedPackages("cm.lao.generated.domeni.kapita.event.dto");

    return new DefaultKafkaConsumerFactory<>(
        props, new StringDeserializer(), new ErrorHandlingDeserializer<>(valueDeserializer));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UserCreatedEventEnvelopeDTO>
      userCreatedEventKafkaListenerContainerFactory(
          ConsumerFactory<String, UserCreatedEventEnvelopeDTO> userCreatedEventConsumerFactory,
          DefaultErrorHandler userCreatedEventErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, UserCreatedEventEnvelopeDTO> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(userCreatedEventConsumerFactory);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    factory.setCommonErrorHandler(userCreatedEventErrorHandler);
    return factory;
  }

  @Bean
  public DefaultErrorHandler userCreatedEventErrorHandler() {
    FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2L);
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);
    errorHandler.addNotRetryableExceptions(
        DeserializationException.class, IllegalArgumentException.class);
    return errorHandler;
  }
}
