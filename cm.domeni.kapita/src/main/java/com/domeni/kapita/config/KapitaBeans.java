package com.domeni.kapita.config;

import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.impl.DebtFactoryImpl;
import com.domeni.kapita.domain.debt.impl.DebtFetcherImpl;
import com.domeni.kapita.domain.demo.DemoFactory;
import com.domeni.kapita.domain.demo.DemoFetcher;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.domain.demo.impl.DemoFactoryImpl;
import com.domeni.kapita.domain.demo.impl.DemoFetcherImpl;
import com.domeni.kapita.domain.transaction.TransactionFactory;
import com.domeni.kapita.domain.transaction.TransactionFetcher;
import com.domeni.kapita.domain.transaction.TransactionRepository;
import com.domeni.kapita.domain.transaction.impl.TransactionFactoryImpl;
import com.domeni.kapita.domain.transaction.impl.TransactionFetcherImpl;
import com.domeni.kapita.domain.user.UserFactory;
import com.domeni.kapita.domain.user.UserRepository;
import com.domeni.kapita.domain.user.impl.UserFactoryImpl;
import com.domeni.kapita.repositories.DebtSpringRepository;
import com.domeni.kapita.repositories.DemoSpringRepository;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import com.domeni.kapita.repositories.UserSpringRepository;
import com.domeni.kapita.repositories.impl.DebtRepositoryImpl;
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
  public DebtFactory debtFactory(
      DebtRepository debtRepository, TransactionFactory transactionFactory, Clock systemClock) {
    return new DebtFactoryImpl(debtRepository, transactionFactory, systemClock);
  }

  @Bean
  public DebtFetcher debtFetcher(DebtRepository debtRepository) {
    return new DebtFetcherImpl(debtRepository);
  }

  @Bean
  public TransactionFetcher transactionFetcher(TransactionRepository transactionRepository) {
    return new TransactionFetcherImpl(transactionRepository);
  }

  @Bean
  public DebtRepository debtRepository(DebtSpringRepository debtSpringRepository) {
    return new DebtRepositoryImpl(debtSpringRepository);
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
