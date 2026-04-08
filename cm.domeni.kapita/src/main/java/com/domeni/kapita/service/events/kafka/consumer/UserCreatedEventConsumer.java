package com.domeni.kapita.service.events.kafka.consumer;

import com.domeni.kapita.kafka.inbound.KafkaInboundConsumer;
import com.domeni.kapita.kafka.inbound.autoconfigure.KafkaInboundBeanNames;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedEventConsumer {

  private final KafkaInboundConsumer kafkaInboundConsumer;

  @KafkaListener(
      topics = "${kapita.messaging.user-created.topic:user-created}",
      groupId = "${spring.kafka.consumer.group-id:kapita-service}",
      containerFactory = KafkaInboundBeanNames.LISTENER_CONTAINER_FACTORY)
  public void handleUserCreatedEvent(
      @Payload byte[] raw,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
      @Header(KafkaHeaders.OFFSET) long offset,
      Acknowledgment acknowledgment) {
    kafkaInboundConsumer.consume(raw, topic, partition, offset, acknowledgment);
  }
}
