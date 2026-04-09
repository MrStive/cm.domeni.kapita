package com.domeni.kapita.kafka.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboundEventEnvelope<T> {

  @Nullable private String eventId;
  @Nullable private String eventType;
  @Nullable private T payload;
}
