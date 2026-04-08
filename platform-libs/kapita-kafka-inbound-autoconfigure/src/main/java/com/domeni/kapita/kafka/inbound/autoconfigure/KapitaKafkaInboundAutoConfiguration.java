package com.domeni.kapita.kafka.inbound.autoconfigure;

import com.domeni.kapita.kafka.inbound.DefaultEventDispatcher;
import com.domeni.kapita.kafka.inbound.DefaultInboundEventEnvelopeReader;
import com.domeni.kapita.kafka.inbound.EventDispatcher;
import com.domeni.kapita.kafka.inbound.InboundEventEnvelopeReader;
import com.domeni.kapita.kafka.inbound.InboundEventHandler;
import com.domeni.kapita.kafka.inbound.InboundEventInbox;
import com.domeni.kapita.kafka.inbound.KafkaInboundConsumer;
import com.domeni.kapita.kafka.inbound.persistence.InboxEvent;
import com.domeni.kapita.kafka.inbound.persistence.InboxEventSpringRepository;
import com.domeni.kapita.kafka.inbound.persistence.JpaInboundEventInbox;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@AutoConfiguration(after = {KafkaAutoConfiguration.class, JacksonAutoConfiguration.class})
@EnableKafka
@EnableConfigurationProperties(KapitaKafkaInboundProperties.class)
@ConditionalOnClass({ObjectMapper.class, ConcurrentKafkaListenerContainerFactory.class})
@Import(KapitaKafkaInboundAutoConfiguration.KapitaKafkaInboundPersistenceConfiguration.class)
public class KapitaKafkaInboundAutoConfiguration {

  @Bean(name = KafkaInboundBeanNames.CONSUMER_FACTORY)
  @ConditionalOnMissingBean(name = KafkaInboundBeanNames.CONSUMER_FACTORY)
  public ConsumerFactory<String, byte[]> kapitaKafkaInboundConsumerFactory(
      KafkaProperties kafkaProperties, KapitaKafkaInboundProperties properties) {
    Map<String, Object> consumerProperties = new HashMap<>(kafkaProperties.buildConsumerProperties());
    consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
    consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    consumerProperties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, properties.getIsolationLevel());

    return new DefaultKafkaConsumerFactory<>(
        consumerProperties, new StringDeserializer(), new ByteArrayDeserializer());
  }

  @Bean(name = KafkaInboundBeanNames.ERROR_HANDLER)
  @ConditionalOnMissingBean(name = KafkaInboundBeanNames.ERROR_HANDLER)
  public DefaultErrorHandler kapitaKafkaInboundErrorHandler(
      KapitaKafkaInboundProperties properties) {
    FixedBackOff fixedBackOff =
        new FixedBackOff(properties.getRetryIntervalMs(), properties.getRetryMaxAttempts());
    DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);
    errorHandler.addNotRetryableExceptions(
        DeserializationException.class, IllegalArgumentException.class);
    return errorHandler;
  }

  @Bean(name = KafkaInboundBeanNames.LISTENER_CONTAINER_FACTORY)
  @ConditionalOnMissingBean(name = KafkaInboundBeanNames.LISTENER_CONTAINER_FACTORY)
  public ConcurrentKafkaListenerContainerFactory<String, byte[]>
      kapitaKafkaInboundListenerContainerFactory(
          @Value("${spring.kafka.listener.auto-startup:true}") boolean autoStartup,
          @Qualifier(KafkaInboundBeanNames.CONSUMER_FACTORY)
              ConsumerFactory<String, byte[]> kapitaKafkaInboundConsumerFactory,
          @Qualifier(KafkaInboundBeanNames.ERROR_HANDLER)
              CommonErrorHandler kapitaKafkaInboundErrorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, byte[]> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kapitaKafkaInboundConsumerFactory);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    factory.setAutoStartup(autoStartup);
    factory.setCommonErrorHandler(kapitaKafkaInboundErrorHandler);
    return factory;
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnSingleCandidate(ObjectMapper.class)
  public InboundEventEnvelopeReader inboundEventEnvelopeReader(ObjectMapper objectMapper) {
    return new DefaultInboundEventEnvelopeReader(objectMapper);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(InboundEventInbox.class)
  @ConditionalOnSingleCandidate(ObjectMapper.class)
  public EventDispatcher eventDispatcher(
      ObjectMapper objectMapper,
      InboundEventEnvelopeReader inboundEventEnvelopeReader,
      List<InboundEventHandler<?>> handlers,
      InboundEventInbox inboundEventInbox) {
    return new DefaultEventDispatcher(
        objectMapper, inboundEventEnvelopeReader, handlers, inboundEventInbox);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(EventDispatcher.class)
  public KafkaInboundConsumer kafkaInboundConsumer(EventDispatcher eventDispatcher) {
    return new KafkaInboundConsumer(eventDispatcher);
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(JpaRepository.class)
  @EnableJpaRepositories(basePackageClasses = InboxEventSpringRepository.class)
  @EntityScan(basePackageClasses = InboxEvent.class)
  static class KapitaKafkaInboundPersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InboundEventInbox inboundEventInbox(InboxEventSpringRepository inboxEventSpringRepository) {
      return new JpaInboundEventInbox(inboxEventSpringRepository);
    }
  }
}
