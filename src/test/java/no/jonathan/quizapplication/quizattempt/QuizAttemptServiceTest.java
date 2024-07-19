package no.jonathan.quizapplication.quizattempt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import net.datafaker.Faker;
import no.jonathan.quizapplication.exception.QuizAttemptNotFoundException;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.shared.QuizAttemptDtoMapper;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.utils.AccessUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

  private Faker faker;

  @InjectMocks private QuizAttemptService underTest;

  @Mock private QuizModuleApi quizModuleApi;
  @Mock private QuizAttemptRepository quizAttemptRepository;
  @Mock private QuizAttemptRepositoryCustom quizAttemptRepositoryCustom;
  @Mock private QuizAttemptDtoMapper quizAttemptDtoMapper;

  @BeforeEach
  void setUp() {
    this.faker = new Faker();
  }

  @Test
  @DisplayName("It should 1 quiz attempts by user")
  void itShouldGetQuizAttemptsByUser_1() {
    // Given
    String title = faker.book().title();
    Authentication mockAuthentication = mock(Authentication.class);
    Pageable pageable = PageRequest.of(0, 10);

    Page<QuizAttemptCoreProjection> mockPage =
        new PageImpl<>(Collections.singletonList(mock(QuizAttemptCoreProjection.class)));

    String name = faker.internet().emailAddress();

    // When
    when(mockAuthentication.getName()).thenReturn(name);

    when(quizAttemptRepositoryCustom.findAll(title, name, pageable)).thenReturn(mockPage);

    // Then
    var result = underTest.getQuizAttemptsByUser(title, mockAuthentication, pageable);

    assertThat(result.getContent().size()).isEqualTo(1);
  }

  @Test
  @DisplayName("It should 0 quiz attempts by user")
  void itShouldGetQuizAttemptsByUser_0() {
    // Given
    String title = faker.book().title();
    Authentication mockAuthentication = mock(Authentication.class);
    Pageable pageable = PageRequest.of(0, 10);

    Page<QuizAttemptCoreProjection> mockPage = new PageImpl<>(Collections.emptyList());

    String name = faker.internet().emailAddress();

    // When
    when(mockAuthentication.getName()).thenReturn(name);

    when(quizAttemptRepositoryCustom.findAll(title, name, pageable)).thenReturn(mockPage);

    // Then
    var result = underTest.getQuizAttemptsByUser(title, mockAuthentication, pageable);

    assertThat(result.getContent().size()).isEqualTo(0);
  }

  @Test
  @DisplayName("It should get quiz attempt by link")
  void itShouldGetQuizAttemptByLink() {
    // Given
    UUID quizAttemptLink = UUID.randomUUID();
    QuizAttemptDto mockQuizAttemptDto = mock(QuizAttemptDto.class);
    Authentication mockAuthentication = mock(Authentication.class);

    String entityCreatedByEmail = faker.internet().emailAddress();

    // When
    when(quizAttemptRepositoryCustom.findByLink(quizAttemptLink))
        .thenReturn(Optional.of(mockQuizAttemptDto));

    when(mockQuizAttemptDto.createdByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    // Then
    var result = underTest.getQuizAttemptByLink(quizAttemptLink, mockAuthentication);

    assertNotNull(result);
  }

  @Test
  @DisplayName("It will throw when quiz attempt dto not found when get quiz attempt by link")
  void itWillThrowWhenQuizAttemptDtoNotFoundWhenGetQuizAttemptByLink() {
    // Given
    UUID quizAttemptLink = UUID.randomUUID();
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizAttemptRepositoryCustom.findByLink(quizAttemptLink)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.getQuizAttemptByLink(quizAttemptLink, mockAuthentication))
        .isInstanceOf(QuizAttemptNotFoundException.class)
        .hasMessage("Quiz attempt with link '" + quizAttemptLink + "' was not found");
  }

  @Test
  @DisplayName("It should create quiz attempt for user")
  void itShouldCreateQuizAttemptForUser() {
    // TODO Why is this needed?
    try (MockedStatic<QuizAttempt> mockedStaticQuizAttempt = mockStatic(QuizAttempt.class)) {
      // Given
      UUID quizLink = UUID.randomUUID();
      User mockUser = mock(User.class);
      Authentication mockAuthentication = mock(Authentication.class);
      Quiz mockQuiz = mock(Quiz.class);
      QuizAttempt mockQuizAttempt = mock(QuizAttempt.class);
      QuizAttemptDto mockQuizAttemptDto = mock(QuizAttemptDto.class);
      QuizAttemptCreateRequest createRequest =
          new QuizAttemptCreateRequest(
              quizLink, LocalDateTime.now(), LocalDateTime.now(), Collections.emptySet());

      String entityCreatedByEmail = faker.internet().emailAddress();

      // When
      when(mockAuthentication.getPrincipal()).thenReturn(mockUser);

      when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

      when(mockQuiz.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

      when(quizModuleApi.findByLink(quizLink)).thenReturn(mockQuiz);

      when(mockQuiz.isShared()).thenReturn(false);

      when(QuizAttempt.fromCreateRequest(mockUser, mockQuiz, createRequest))
          .thenReturn(mockQuizAttempt);

      when(quizAttemptRepository.save(mockQuizAttempt)).thenReturn(mockQuizAttempt);

      when(quizAttemptDtoMapper.apply(mockQuizAttempt)).thenReturn(mockQuizAttemptDto);

      // Then
      var result = underTest.createQuizAttemptForUser(createRequest, mockAuthentication);

      assertNotNull(result);
    }
  }

  @Test
  @DisplayName("It should create quiz attempt for null user")
  void itShouldCreateQuizAttemptForNullUser() {
    // TODO Why is this needed?
    try (MockedStatic<QuizAttempt> mockedStaticQuizAttempt = mockStatic(QuizAttempt.class);
        MockedStatic<AccessUtil> mockedStaticAccessUtil = mockStatic(AccessUtil.class)) {
      // Given
      UUID quizLink = UUID.randomUUID();
      Quiz mockQuiz = mock(Quiz.class);
      QuizAttempt mockQuizAttempt = mock(QuizAttempt.class);
      QuizAttemptDto mockQuizAttemptDto = mock(QuizAttemptDto.class);
      QuizAttemptCreateRequest createRequest =
          new QuizAttemptCreateRequest(
              quizLink, LocalDateTime.now(), LocalDateTime.now(), Collections.emptySet());

      // When
      when(quizModuleApi.findByLink(quizLink)).thenReturn(mockQuiz);

      when(mockQuiz.isShared()).thenReturn(true);

      when(QuizAttempt.fromCreateRequest(null, mockQuiz, createRequest))
          .thenReturn(mockQuizAttempt);

      when(quizAttemptRepository.save(mockQuizAttempt)).thenReturn(mockQuizAttempt);

      when(quizAttemptDtoMapper.apply(mockQuizAttempt)).thenReturn(mockQuizAttemptDto);

      // Then
      var result = underTest.createQuizAttemptForUser(createRequest, null);

      assertNotNull(result);
      mockedStaticAccessUtil.verify(
          () -> AccessUtil.verifyUserHasAccessToEntity(any(), any()), never());
    }
  }
}
