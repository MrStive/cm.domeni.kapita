package com.domeni.kapita.jpa.autoconfigure;

import jakarta.persistence.EntityManagerFactory;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@AutoConfiguration(after = DataSourceAutoConfiguration.class)
@ConditionalOnBean(DataSource.class)
@ConditionalOnClass({LocalContainerEntityManagerFactoryBean.class, PersistenceProvider.class})
public class KapitaEclipseLinkJpaAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(LocalContainerEntityManagerFactoryBean.class)
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, Environment environment, ListableBeanFactory beanFactory) {
    boolean showSql = environment.getProperty("spring.jpa.show-sql", Boolean.class, false);
    boolean generateDdl = environment.getProperty("spring.jpa.generate-ddl", Boolean.class, false);

    EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
    jpaVendorAdapter.setShowSql(showSql);
    jpaVendorAdapter.setGenerateDdl(generateDdl);

    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
    entityManagerFactoryBean.setPersistenceProviderClass(PersistenceProvider.class);
    entityManagerFactoryBean.setPackagesToScan(resolvePackagesToScan(beanFactory));
    entityManagerFactoryBean.setJpaPropertyMap(resolveJpaProperties(environment));
    return entityManagerFactoryBean;
  }

  @Bean
  @ConditionalOnMissingBean(PlatformTransactionManager.class)
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  private static String[] resolvePackagesToScan(ListableBeanFactory beanFactory) {
    List<String> autoConfiguredPackages =
        AutoConfigurationPackages.has(beanFactory)
            ? AutoConfigurationPackages.get(beanFactory)
            : List.of();
    List<String> entityScanPackages = EntityScanPackages.get(beanFactory).getPackageNames();
    return java.util.stream.Stream.concat(autoConfiguredPackages.stream(), entityScanPackages.stream())
        .distinct()
        .toArray(String[]::new);
  }

  private static Map<String, Object> resolveJpaProperties(Environment environment) {
    Map<String, String> configuredProperties =
        Binder.get(environment)
            .bind("spring.jpa.properties", Bindable.mapOf(String.class, String.class))
            .orElseGet(Map::of);

    Map<String, Object> jpaProperties = new LinkedHashMap<>(configuredProperties);
    jpaProperties.putIfAbsent("eclipselink.weaving", "false");
    jpaProperties.putIfAbsent("eclipselink.ddl-generation", "none");
    jpaProperties.putIfAbsent("eclipselink.target-database", "PostgreSQL");
    jpaProperties.putIfAbsent("eclipselink.logging.level", "WARNING");
    return jpaProperties;
  }
}
