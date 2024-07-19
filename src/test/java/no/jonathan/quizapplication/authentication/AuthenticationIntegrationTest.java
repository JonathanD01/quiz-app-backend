package no.jonathan.quizapplication.authentication;

import net.datafaker.Faker;
import no.jonathan.quizapplication.container.PostgreSQLContainerInitializer;
import no.jonathan.quizapplication.response.ResponseType;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.spring.sql.QuickPerfSqlConfig;
import org.quickperf.sql.annotation.ExpectInsert;
import org.quickperf.sql.annotation.ExpectSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@Import(QuickPerfSqlConfig.class)
@QuickPerfTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationIntegrationTest {

  private final Faker faker = new Faker();
  private final RegistrationRequest registrationRequest =
      new RegistrationRequest(
          faker.name().firstName(),
          faker.name().lastName(),
          "static-email@spring.no",
          "static-password123");
  @LocalServerPort int port;
  private WebTestClient webTestClient;
  @Autowired private AuthenticationService service;

  @Autowired private ResponseUtil responseUtil;

  @BeforeEach
  void setUp() {
    webTestClient =
        WebTestClient.bindToController(new AuthenticationController(service, responseUtil))
            .configureClient()
            .baseUrl(String.format("http://localhost:%s/api/v1/auth", port))
            .build();
  }

  @Order(1)
  @Test
  @DisplayName("It should successfully register a user with a 200 accepted response")
  @ExpectInsert
  @ExpectSelect(2)
  void itShouldRegister() {
    // Given
    // When
    // Then
    webTestClient
        .post()
        .uri("/register")
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(registrationRequest), RegistrationRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .isEmpty();
  }

  @Order(2)
  @Test
  @DisplayName("It should authenticate")
  @ExpectSelect
  void itShouldAuthenticate() {
    // Given
    var authenticationRequest =
        new AuthenticationRequest(registrationRequest.email(), registrationRequest.password());

    // When
    // Then
    webTestClient
        .post()
        .uri("/authenticate")
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.token")
        .isNotEmpty();
  }
}
