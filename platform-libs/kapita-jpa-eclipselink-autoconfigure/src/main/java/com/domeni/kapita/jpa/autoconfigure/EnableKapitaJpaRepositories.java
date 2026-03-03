package com.domeni.kapita.jpa.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableJpaRepositories(repositoryBaseClass = SoftDeleteJpaRepositoryImpl.class)
public @interface EnableKapitaJpaRepositories {

  @AliasFor(annotation = EnableJpaRepositories.class, attribute = "basePackages")
  String[] basePackages() default {};

  @AliasFor(annotation = EnableJpaRepositories.class, attribute = "basePackageClasses")
  Class<?>[] basePackageClasses() default {};
}
