package com.domeni.kapita.config;

import com.domeni.kapita.api.interceptor.SubscriptionAccessHandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final SubscriptionAccessHandlerInterceptor subscriptionAccessHandlerInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(subscriptionAccessHandlerInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/demo/**",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html");
  }
}
