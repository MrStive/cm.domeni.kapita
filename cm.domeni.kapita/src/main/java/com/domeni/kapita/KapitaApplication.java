package com.domeni.kapita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KapitaApplication {

  public static void main(String[] args) {
    SpringApplication.run(KapitaApplication.class, args);
  }
}
