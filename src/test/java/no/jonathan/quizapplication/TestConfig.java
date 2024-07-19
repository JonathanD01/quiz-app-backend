package no.jonathan.quizapplication;

import no.jonathan.quizapplication.config.ApplicationAuditAware;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

@TestConfiguration
public class TestConfig {

  @Bean
  public AuditorAware<String> auditorAware() {
    return new ApplicationAuditAware();
  }
}
