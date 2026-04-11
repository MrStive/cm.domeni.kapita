package com.domeni.kapita.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, @Value("${kapita.security.expose-docs:false}") boolean exposeDocs)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .authorizeHttpRequests(
            authorize -> {
              var registry =
                  authorize
                      .requestMatchers(HttpMethod.OPTIONS, "/**")
                      .permitAll()
                      .requestMatchers("/actuator/health", "/actuator/info")
                      .permitAll();

              if (exposeDocs) {
                registry
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll();
              }

              registry
                  .requestMatchers(HttpMethod.GET, "/demo")
                  .hasAuthority("SCOPE_demo:read:all")
                  .requestMatchers(HttpMethod.GET, "/demo/*")
                  .hasAuthority("SCOPE_demo:read")
                  .requestMatchers(HttpMethod.GET, "/debt")
                  .hasAuthority("SCOPE_debt:read")
                  .requestMatchers(HttpMethod.GET, "/transaction/balance")
                  .hasAuthority("SCOPE_transaction:read:balance")
                  .requestMatchers(HttpMethod.GET, "/transaction/amount")
                  .hasAuthority("SCOPE_transaction:read:amount")
                  .requestMatchers(HttpMethod.POST, "/demo")
                  .hasAuthority("SCOPE_demo:create")
                  .requestMatchers(HttpMethod.POST, "/debt")
                  .hasAuthority("SCOPE_debt:create")
                  .requestMatchers(HttpMethod.POST, "/transaction")
                  .hasAuthority("SCOPE_transaction:create")
                  .anyRequest()
                  .denyAll();
            });
    return http.build();
  }
}
