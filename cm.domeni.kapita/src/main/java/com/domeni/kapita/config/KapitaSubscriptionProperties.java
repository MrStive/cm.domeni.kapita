package com.domeni.kapita.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kapita.subscription")
public class KapitaSubscriptionProperties {
  private int trialPeriodDays = 14;
  private Cache cache = new Cache();
  private Scheduler scheduler = new Scheduler();

  @Getter
  @Setter
  public static class Cache {
    private int defaultTtlSeconds = 300;
  }

  @Getter
  @Setter
  public static class Scheduler {
    private boolean enabled = true;
    private String cron = "0 0 * * * *";
  }
}
