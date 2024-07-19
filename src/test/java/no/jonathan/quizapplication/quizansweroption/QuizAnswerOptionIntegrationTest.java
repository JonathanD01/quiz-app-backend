package no.jonathan.quizapplication.quizansweroption;

import static org.junit.jupiter.api.Assertions.*;

import no.jonathan.quizapplication.IntegrationTestWithUserInfo;
import no.jonathan.quizapplication.response.ResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quickperf.sql.annotation.ExpectDelete;
import org.quickperf.sql.annotation.ExpectInsert;
import org.quickperf.sql.annotation.ExpectSelect;
import org.quickperf.sql.annotation.ExpectUpdate;
import org.springframework.http.HttpHeaders;

class QuizAnswerOptionIntegrationTest extends IntegrationTestWithUserInfo {

  public QuizAnswerOptionIntegrationTest() {
    super("api/v1/quizansweroption");
  }

  @Test
  @DisplayName("Can create quiz answer option")
  @ExpectSelect(3)
  @ExpectInsert
  void canCreateQuizAnswerOption() {
    // Given
    Long quizQuestionId = 100L;
    String text = FAKER.book().title();
    boolean correct = FAKER.random().nextBoolean();
    QuizAnswerOptionCreateRequest createRequest =
        new QuizAnswerOptionCreateRequest(quizQuestionId, text, correct);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

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
        .jsonPath("$.result.text")
        .isEqualTo(text)
        .jsonPath("$.result.correct")
        .isEqualTo(correct);
  }

  @Test
  @ExpectSelect(2)
  @ExpectUpdate
  void canUpdateQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = 200L;
    String text = FAKER.book().title();
    boolean correct = FAKER.random().nextBoolean();
    QuizAnswerOptionUpdateRequest updateRequest = new QuizAnswerOptionUpdateRequest(text, correct);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

    // When
    // Then
    webTestClient
        .patch()
        .uri(path + "/{quizAnswerOptionId}", quizAnswerOptionId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizAnswerOptionId)
        .jsonPath("$.result.text")
        .isEqualTo(text)
        .jsonPath("$.result.correct")
        .isEqualTo(correct);
  }

  @Test
  @ExpectSelect(2)
  @ExpectDelete
  void canDeleteQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = 300L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

    // When
    // Then
    webTestClient
        .delete()
        .uri(path + "/{quizAnswerOptionId}", quizAnswerOptionId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result")
        .isEqualTo(quizAnswerOptionId);
  }
}
