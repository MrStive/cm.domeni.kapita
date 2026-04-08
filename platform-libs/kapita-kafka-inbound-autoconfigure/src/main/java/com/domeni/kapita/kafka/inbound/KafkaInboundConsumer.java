package com.domeni.kapita.kafka.inbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public class KafkaInboundConsumer {

  private final EventDispatcher eventDispatcher;

  public void consume(
      byte[] raw,
      String topic,
      int partition,
      long offset,
      Acknowledgment acknowledgment) {
    InboundEventContext context = new InboundEventContext(topic, partition, offset);
    eventDispatcher.dispatch(raw, context);
    acknowledgment.acknowledge();
    log.debug(
        "Acknowledged inbound event from topic={} partition={} offset={}",
        topic,
        partition,
        offset);
  }
}
