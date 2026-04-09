package com.domeni.kapita.config;

import com.domeni.kapita.domain.demo.DemoFactory;
import com.domeni.kapita.domain.demo.DemoFetcher;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.domain.demo.impl.DemoFactoryImpl;
import com.domeni.kapita.domain.demo.impl.DemoFetcherImpl;
import com.domeni.kapita.domain.transaction.TransactionBalanceFetcher;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.impl.TransactionBalanceFetcherImpl;
import com.domeni.kapita.domain.transaction.impl.TransactionFactoryImpl;
import com.domeni.kapita.domain.user.UserFactory;
import com.domeni.kapita.domain.user.UserRepository;
import com.domeni.kapita.domain.user.impl.UserFactoryImpl;
import com.domeni.kapita.repositories.DemoSpringRepository;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import com.domeni.kapita.repositories.UserSpringRepository;
import com.domeni.kapita.repositories.impl.DemoRepositoryImpl;
import com.domeni.kapita.repositories.impl.TransactionRepositoryImpl;
import com.domeni.kapita.repositories.impl.UserRepositoryImpl;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class KapitaBeans {
  @Bean
  public DemoFactory demoFactory(DemoRepository demoRepository) {
    return new DemoFactoryImpl(demoRepository);
  }

  @Bean
  public DemoRepository demoRepository(DemoSpringRepository demoSpringRepository) {
    return new DemoRepositoryImpl(demoSpringRepository);
  }

  @Bean
  public DemoFetcher demoFetcher(DemoRepository demoRepository) {
    return new DemoFetcherImpl(demoRepository);
  }

  @Bean
  public Clock systemClock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  public TransactionFactory transactionFactory(
      TransactionRepository transactionRepository, Clock systemClock) {
    return new TransactionFactoryImpl(transactionRepository, systemClock);
  }

  @Bean
  public TransactionBalanceFetcher transactionBalanceFetcher(
      TransactionRepository transactionRepository) {
    return new TransactionBalanceFetcherImpl(transactionRepository);
  }

  @Bean
  public TransactionRepository transactionRepository(
      TransactionSpringRepository transactionSpringRepository) {
    return new TransactionRepositoryImpl(transactionSpringRepository);
  }

  @Bean
  public UserFactory userFactory(UserRepository userRepository) {
    return new UserFactoryImpl(userRepository);
  }

  @Bean
  public UserRepository userRepository(UserSpringRepository userSpringRepository) {
    return new UserRepositoryImpl(userSpringRepository);
  }
}
