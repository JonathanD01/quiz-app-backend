package no.jonathan.quizapplication.quizattempt;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import no.jonathan.quizapplication.IntegrationTestWithUserInfo;
import no.jonathan.quizapplication.response.ResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quickperf.sql.annotation.ExpectInsert;
import org.quickperf.sql.annotation.ExpectSelect;
import org.springframework.http.HttpHeaders;

class QuizAttemptIntegrationTest extends IntegrationTestWithUserInfo {

  public QuizAttemptIntegrationTest() {
    super("api/v1/quizattempts");
  }

  @Test
  @DisplayName("Can get quiz attempts by user")
  @ExpectSelect(3)
  void canGetQuizAttemptsByUser() {
    // Given
    int page = 0;
    int size = 10;

    String pageParam = String.format("?page=%s&size=%s", page, size);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + pageParam)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.content[0].id")
        .exists()
        .jsonPath("$.result.content[0].link")
        .exists()
        .jsonPath("$.result.content[0].title")
        .exists()
        .jsonPath("$.result.content[0].description")
        .exists()
        .jsonPath("$.result.page.totalElements")
        .isNumber()
        .jsonPath("$.result.page.size")
        .isEqualTo(size)
        .jsonPath("$.result.page.number")
        .isEqualTo(page);
  }

  @Test
  @DisplayName("Can get quiz attempts by user with quiz title")
  @ExpectSelect(3)
  void canGetQuizAttemptsByUserWithQuizTitle() {
    // Given
    int page = 0;
    int size = 10;
    int totalElements = 3;
    String title = "%";

    String params = String.format("?page=%s&size=%s&title=%s", page, size, title);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + params)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.content")
        .isArray()
        .jsonPath("$.result.page.totalElements")
        .isEqualTo(totalElements)
        .jsonPath("$.result.page.size")
        .isEqualTo(size)
        .jsonPath("$.result.page.number")
        .isEqualTo(page);
  }

  @Test
  @DisplayName("Can get quiz attempt by user from link")
  @ExpectSelect(2)
  void canGetQuizAttemptByUser() {
    // Given
    String link = "550e8400-e29b-41d4-a716-446655440010";

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + "/{link}", link)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(1000)
        .jsonPath("$.result.link")
        .isEqualTo(link)
        .jsonPath("$.result.quizDto.title")
        .isEqualTo("50% smart")
        .jsonPath("$.result.quizDto.description")
        .isEqualTo("A quiz about general knowledge.");
  }

  @Test
  @DisplayName("Can create quiz attempt")
  @ExpectSelect(3)
  @ExpectInsert(3)
  void canCreateQuizAttemptForUser() {
    // Given
    UUID quizLink = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = startTime.plusHours(1);

    QuizAttemptCreateRequest createRequest =
        new QuizAttemptCreateRequest(
            quizLink,
            startTime,
            endTime,
            Set.of(new QuizUserAnswer(100L, Set.of(100L)), new QuizUserAnswer(200L, Set.of(400L))));

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .post()
        .uri(path)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .bodyValue(createRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isNumber()
        .jsonPath("$.result.link")
        .isNotEmpty()
        .jsonPath("$.result.quizDto.link")
        .isEqualTo(quizLink.toString())
        .jsonPath("$.result.startTime")
        .isNotEmpty()
        .jsonPath("$.result.endTime")
        .isNotEmpty()
        .jsonPath("$.result.createdByEmail")
        .isEqualTo(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER)
        .jsonPath("$.result.userAnswers")
        .isArray()
        .jsonPath("$.result.userAnswers[0].quizAnswerOptionIds")
        .isEqualTo(100)
        .jsonPath("$.result.userAnswers[1].quizAnswerOptionIds")
        .isEqualTo(400);
  }
}
