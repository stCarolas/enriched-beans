package io.github.stcarolas.enrichedbeans.examples.immutablescriteriaspring;

import org.immutables.criteria.backend.Backend;
import org.immutables.criteria.inmemory.InMemoryBackend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Backend backend(){
    return new InMemoryBackend();
  }

}
