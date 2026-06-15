package com.domeni.kapita.config;

import cm.domeni.generated.domeni.kapita.payment.api.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class PaymentClientConfig {

  @Bean
  public PaymentApi paymentApi(
      @Value("${kapita.payment.url:http://localhost:8081}") String baseUrl) {
    RestClient restClient = RestClient.builder().baseUrl(baseUrl).build();
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(PaymentApi.class);
  }
}
