package no.jonathan.quizapplication;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;

import net.datafaker.Faker;
import no.jonathan.quizapplication.config.JwtService;
import no.jonathan.quizapplication.container.PostgreSQLContainerInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.spring.sql.QuickPerfSqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("test")
@Import(QuickPerfSqlConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {PostgreSQLContainerInitializer.class})
@Sql(value = "classpath:/it-data-2.sql", executionPhase = BEFORE_TEST_CLASS)
@QuickPerfTest
public abstract class IntegrationTestWithUserInfo {

  protected static final String USER_WITH_ROLE_USER = "john.doe@example.com"; // User with role NONE

  protected static final String USER_WITH_ROLE_ADMIN =
      "jane.smith@example.com"; // User with role ADMIN
  protected static final Faker FAKER = new Faker();
  protected final String path;
  @Autowired protected JwtService jwtService;
  @Autowired protected WebTestClient webTestClient;
  @LocalServerPort private int port;

  public IntegrationTestWithUserInfo(String path) {
    this.path = path;
  }

  protected String authorizationHeader(String jwtToken) {
    return String.format("Bearer %s", jwtToken);
  }

  @BeforeEach
  void setUp() {
    this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }
}
