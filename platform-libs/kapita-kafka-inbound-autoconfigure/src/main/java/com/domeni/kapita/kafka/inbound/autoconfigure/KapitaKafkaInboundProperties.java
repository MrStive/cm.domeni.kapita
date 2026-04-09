package com.domeni.kapita.kafka.inbound.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kapita.kafka.inbound")
public class KapitaKafkaInboundProperties {

  private String autoOffsetReset = "earliest";
  private String isolationLevel = "read_committed";
  private long retryIntervalMs = 1000L;
  private long retryMaxAttempts = 2L;

  public String getAutoOffsetReset() {
    return autoOffsetReset;
  }

  public void setAutoOffsetReset(String autoOffsetReset) {
    this.autoOffsetReset = autoOffsetReset;
  }

  public String getIsolationLevel() {
    return isolationLevel;
  }

  public void setIsolationLevel(String isolationLevel) {
    this.isolationLevel = isolationLevel;
  }

  public long getRetryIntervalMs() {
    return retryIntervalMs;
  }

  public void setRetryIntervalMs(long retryIntervalMs) {
    this.retryIntervalMs = retryIntervalMs;
  }

  public long getRetryMaxAttempts() {
    return retryMaxAttempts;
  }

  public void setRetryMaxAttempts(long retryMaxAttempts) {
    this.retryMaxAttempts = retryMaxAttempts;
  }
}
