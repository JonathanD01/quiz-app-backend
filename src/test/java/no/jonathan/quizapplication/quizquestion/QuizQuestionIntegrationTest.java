package no.jonathan.quizapplication.quizquestion;

import no.jonathan.quizapplication.IntegrationTestWithUserInfo;
import no.jonathan.quizapplication.response.ResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quickperf.sql.annotation.ExpectDelete;
import org.quickperf.sql.annotation.ExpectInsert;
import org.quickperf.sql.annotation.ExpectSelect;
import org.quickperf.sql.annotation.ExpectUpdate;
import org.springframework.http.HttpHeaders;

class QuizQuestionIntegrationTest extends IntegrationTestWithUserInfo {

  public QuizQuestionIntegrationTest() {
    super("api/v1/quizquestion");
  }

  @Test
  @DisplayName("Can add question to quiz")
  @ExpectSelect(3)
  @ExpectInsert
  void canCreateQuizQuestion() {
    // Given
    String question = FAKER.book().title();
    QuizQuestionCreateRequest createRequest = new QuizQuestionCreateRequest(100L, question);

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
        .exists()
        .jsonPath("$.result.text")
        .isEqualTo(question)
        .jsonPath("$.result.quizAnswerOptions")
        .isEmpty();
  }

  @Test
  @DisplayName("Can update quiz question")
  @ExpectSelect(2)
  @ExpectUpdate
  void canUpdateQuizQuestion() {
    // Given
    Long quizQuestionId = 200L;
    String newQuestion = FAKER.book().title();
    QuizQuestionUpdateRequest updateRequest = new QuizQuestionUpdateRequest(newQuestion);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .patch()
        .uri(path + "/{quizQuestionId}", quizQuestionId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizQuestionId)
        .jsonPath("$.result.text")
        .isEqualTo(newQuestion)
        .jsonPath("$.result.quizAnswerOptions")
        .isArray()
        .jsonPath("$.result.quizAnswerOptions")
        .isNotEmpty();
  }

  @Test
  @DisplayName("Can delete quiz question")
  @ExpectSelect(2)
  @ExpectDelete(2)
  void canDeleteQuizQuestion() {
    // Given
    Long quizQuestionId = 700L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .delete()
        .uri(path + "/{quizQuestionId}", quizQuestionId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result")
        .isEqualTo(quizQuestionId);
  }
}
