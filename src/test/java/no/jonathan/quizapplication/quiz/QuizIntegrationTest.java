package no.jonathan.quizapplication.quiz;

import java.io.IOException;
import java.util.UUID;
import no.jonathan.quizapplication.IntegrationTestWithUserInfo;
import no.jonathan.quizapplication.response.ResponseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quickperf.sql.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

class QuizIntegrationTest extends IntegrationTestWithUserInfo {

  public QuizIntegrationTest() {
    super("api/v1/quizzes");
  }

  @Test
  @DisplayName("Can get (2) quizzes by user")
  @ExpectSelect(3)
  void canGetQuizzesByUser() {
    // Given
    int page = 0;
    int size = 2;
    int totalElements = 2;

    String pageParam = String.format("?page=%s&size=%s", page, size);

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

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
        .jsonPath("$.result.page.totalElements")
        .isEqualTo(totalElements)
        .jsonPath("$.result.page.size")
        .isEqualTo(size)
        .jsonPath("$.result.page.number")
        .isEqualTo(page);
  }

  @Test
  @DisplayName("Can get quiz by id")
  @ExpectSelect(2)
  void canGetQuizById() {
    // Given
    Long quizId = 100L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizId)
        .jsonPath("$.result.title")
        .isEqualTo("50% smart");
  }

  @Test
  @DisplayName("Can admin user get another users quiz by id")
  @ExpectSelect(2)
  void canAdminUserGetAnotherUsersQuizById() {
    // Given
    Long quizId = 100L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizId)
        .jsonPath("$.result.title")
        .isEqualTo("50% smart");
  }

  @Test
  @DisplayName("It should get quiz by link")
  @ExpectSelect(2)
  void canGetQuizByLink() {
    // Given
    UUID quizLink = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .get()
        .uri(path + "/link/{quizLink}", quizLink)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.link")
        .isEqualTo(quizLink.toString())
        .jsonPath("$.result.title")
        .isEqualTo("50% smart");
  }

  @Test
  @DisplayName("Can a non authenticated user get a shared quiz by link")
  @ExpectSelect
  void canNonAuthenticatedUserGetSharedQuizByLink() {
    // Given
    UUID quizLink = UUID.fromString("550e8400-e29b-41d4-a716-446655440005");

    // When
    // Then
    webTestClient
        .get()
        .uri(path + "/link/{quizLink}", quizLink)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.link")
        .isEqualTo(quizLink.toString())
        .jsonPath("$.result.title")
        .isEqualTo("Shared Quiz");
  }

  @Test
  @DisplayName("Can create a quiz with AI")
  @ExpectMaxSelect(4)
  void canCreateQuizWithAI() throws IOException {
    // Given
    ClassPathResource file = new ClassPathResource("chocolate_quiz.txt");
    String language = "french";

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("file", new FileSystemResource(file.getFile()));
    params.add("language", language);

    // When
    // Then
    webTestClient
        .post()
        .uri(path + "/ai")
        .header(MediaType.MULTIPART_FORM_DATA_VALUE)
        .bodyValue(params)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$")
        .isNumber();
  }

  @Test
  @DisplayName("Can a user update their quiz")
  @ExpectSelect(2)
  @ExpectUpdate
  void canUpdateQuiz() {
    // Given
    Long quizId = 200L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    String title = FAKER.book().title();
    String description = FAKER.lorem().characters(100, 200);
    boolean shared = FAKER.random().nextBoolean();

    QuizUpdateRequest updateRequest = new QuizUpdateRequest(title, description, shared);

    // When
    // Then
    webTestClient
        .patch()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizId)
        .jsonPath("$.result.title")
        .isEqualTo(title)
        .jsonPath("$.result.description")
        .isEqualTo(description)
        .jsonPath("$.result.shared")
        .isEqualTo(shared);
  }

  @Test
  @DisplayName("Can a admin user update another users quiz")
  @ExpectSelect(2)
  @ExpectUpdate
  void canAdminUserUpdateAnotherUsersQuiz() {
    // Given
    Long quizId = 500L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

    String title = FAKER.book().title();
    String description = FAKER.lorem().characters(100, 200);
    boolean shared = FAKER.random().nextBoolean();

    QuizUpdateRequest updateRequest = new QuizUpdateRequest(title, description, shared);

    // When
    // Then
    webTestClient
        .patch()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .bodyValue(updateRequest)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result.id")
        .isEqualTo(quizId)
        .jsonPath("$.result.title")
        .isEqualTo(title)
        .jsonPath("$.result.description")
        .isEqualTo(description)
        .jsonPath("$.result.shared")
        .isEqualTo(shared);
  }

  @Test
  @DisplayName("Can a user delete a quiz")
  @ExpectSelect(5)
  @ExpectDelete(5)
  void canDeleteQuiz() {
    // Given
    Long quizId = 700L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_USER);

    // When
    // Then
    webTestClient
        .delete()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result")
        .isEqualTo(quizId);
  }

  @Test
  @DisplayName("Can a user admin delete another users quiz")
  @ExpectSelect(5)
  @ExpectDelete(5)
  void canAdminUserDeleteAnotherUsersQuiz() {
    // Given
    Long quizId = 800L;

    String token =
        jwtService.generateTokenFromUsernameOnly(IntegrationTestWithUserInfo.USER_WITH_ROLE_ADMIN);

    // When
    // Then
    webTestClient
        .delete()
        .uri(path + "/{quizId}", quizId)
        .header(HttpHeaders.AUTHORIZATION, authorizationHeader(token))
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.response")
        .isEqualTo(ResponseType.SUCCESS.name())
        .jsonPath("$.result")
        .isEqualTo(quizId);
  }
}
