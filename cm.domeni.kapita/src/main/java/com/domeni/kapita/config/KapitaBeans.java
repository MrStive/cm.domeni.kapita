package com.domeni.kapita.config;

import com.domeni.kapita.domain.debt.DebtFactory;
import com.domeni.kapita.domain.debt.DebtFetcher;
import com.domeni.kapita.domain.debt.DebtRepository;
import com.domeni.kapita.domain.debt.DebtUpdater;
import com.domeni.kapita.domain.debt.impl.DebtFactoryImpl;
import com.domeni.kapita.domain.debt.impl.DebtFetcherImpl;
import com.domeni.kapita.domain.debt.impl.DebtUpdaterImpl;
import com.domeni.kapita.domain.demo.DemoFactory;
import com.domeni.kapita.domain.demo.DemoFetcher;
import com.domeni.kapita.domain.demo.DemoRepository;
import com.domeni.kapita.domain.demo.impl.DemoFactoryImpl;
import com.domeni.kapita.domain.demo.impl.DemoFetcherImpl;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFactory;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanFetcher;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionPlanRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionRepository;
import com.domeni.kapita.domain.subscriptionplan.SubscriptionUpdater;
import com.domeni.kapita.domain.subscriptionplan.impl.SubscriptionFactoryImpl;
import com.domeni.kapita.domain.subscriptionplan.impl.SubscriptionFetcherImpl;
import com.domeni.kapita.domain.subscriptionplan.impl.SubscriptionPlanFactoryImpl;
import com.domeni.kapita.domain.subscriptionplan.impl.SubscriptionPlanFetcherImpl;
import com.domeni.kapita.domain.subscriptionplan.impl.SubscriptionUpdaterImpl;
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
import com.domeni.kapita.repositories.SubscriptionPlanSpringRepository;
import com.domeni.kapita.repositories.SubscriptionSpringRepository;
import com.domeni.kapita.repositories.TransactionSpringRepository;
import com.domeni.kapita.repositories.UserSpringRepository;
import com.domeni.kapita.repositories.impl.DebtRepositoryImpl;
import com.domeni.kapita.repositories.impl.DemoRepositoryImpl;
import com.domeni.kapita.repositories.impl.SubscriptionPlanRepositoryImpl;
import com.domeni.kapita.repositories.impl.SubscriptionRepositoryImpl;
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
  public DebtFactory debtFactory(DebtRepository debtRepository, Clock systemClock) {
    return new DebtFactoryImpl(debtRepository, systemClock);
  }

  @Bean
  public DebtFetcher debtFetcher(DebtRepository debtRepository) {
    return new DebtFetcherImpl(debtRepository);
  }

  @Bean
  public DebtUpdater debtSettler(
      DebtRepository debtRepository, TransactionFactory transactionFactory) {
    return new DebtUpdaterImpl(debtRepository, transactionFactory);
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
  public SubscriptionPlanFactory subscriptionPlanFactory(
      SubscriptionPlanRepository subscriptionPlanRepository, Clock systemClock) {
    return new SubscriptionPlanFactoryImpl(subscriptionPlanRepository, systemClock);
  }

  @Bean
  public SubscriptionPlanRepository subscriptionPlanRepository(
      SubscriptionPlanSpringRepository subscriptionPlanSpringRepository) {
    return new SubscriptionPlanRepositoryImpl(subscriptionPlanSpringRepository);
  }

  @Bean
  public SubscriptionPlanFetcher subscriptionPlanFetcher(
      SubscriptionPlanRepository subscriptionPlanRepository) {
    return new SubscriptionPlanFetcherImpl(subscriptionPlanRepository);
  }

  @Bean
  public SubscriptionFactory subscriptionFactory(
      SubscriptionRepository subscriptionRepository, Clock systemClock) {
    return new SubscriptionFactoryImpl(subscriptionRepository, systemClock);
  }

  @Bean
  public SubscriptionRepository subscriptionRepository(
      SubscriptionSpringRepository subscriptionSpringRepository) {
    return new SubscriptionRepositoryImpl(subscriptionSpringRepository);
  }

  @Bean
  public SubscriptionFetcher subscriptionFetcher(SubscriptionRepository subscriptionRepository) {
    return new SubscriptionFetcherImpl(subscriptionRepository);
  }

  @Bean
  public SubscriptionUpdater subscriptionUpdater(SubscriptionRepository subscriptionRepository) {
    return new SubscriptionUpdaterImpl(subscriptionRepository);
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
