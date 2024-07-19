package no.jonathan.quizapplication.quizquestion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import net.datafaker.Faker;
import no.jonathan.quizapplication.exception.QuizQuestionNotFoundException;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.shared.QuizQuestionDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class QuizQuestionServiceTest {

  private static final Faker FAKER = new Faker();

  @InjectMocks private QuizQuestionService underTest;

  @Mock private QuizModuleApi quizModuleApi;
  @Mock private QuizQuestionRepository quizQuestionRepository;
  @Mock private QuizQuestionRepositoryCustom quizQuestionRepositoryCustom;
  @Mock private QuizQuestionDtoMapper quizQuestionDtoMapper;

  @Test
  @DisplayName("It should create quiz question")
  void itShouldCreateQuizQuestion() {
    // TODO Why is this needed?
    try (MockedStatic<QuizQuestion> mockedStaticQuizQuestion = mockStatic(QuizQuestion.class)) {
      // Given
      Long quizId = FAKER.random().nextLong(0, 100);
      Quiz mockQuiz = mock(Quiz.class);
      QuizQuestion mockQuizQuestion = mock(QuizQuestion.class);
      QuizQuestionDto mockQuizQuestionDto = mock(QuizQuestionDto.class);
      Authentication mockAuthentication = mock(Authentication.class);
      QuizQuestionCreateRequest createRequest =
          new QuizQuestionCreateRequest(quizId, "new question");

      String entityCreatedByEmail = FAKER.internet().emailAddress();

      // When
      when(quizModuleApi.findById(quizId)).thenReturn(mockQuiz);

      when(QuizQuestion.fromCreateRequest(mockQuiz, createRequest)).thenReturn(mockQuizQuestion);

      when(mockQuiz.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

      when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

      when(quizQuestionRepository.save(mockQuizQuestion)).thenReturn(mockQuizQuestion);

      when(quizQuestionDtoMapper.apply(mockQuizQuestion)).thenReturn(mockQuizQuestionDto);

      // Then
      var result = underTest.createQuizQuestion(createRequest, mockAuthentication);

      assertNotNull(result);
    }
  }

  @Test
  @DisplayName("It should update quiz question")
  void itShouldUpdateQuizQuestion() {
    // Given
    Long quizQuestionId = FAKER.random().nextLong(0, 100);
    QuizQuestion mockQuizQuestion = mock(QuizQuestion.class);
    QuizQuestionDto mockQuizQuestionDto = mock(QuizQuestionDto.class);
    Authentication mockAuthentication = mock(Authentication.class);
    QuizQuestionUpdateRequest updateRequest = new QuizQuestionUpdateRequest("new question");

    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizQuestionRepositoryCustom.findById(quizQuestionId))
        .thenReturn(Optional.of(mockQuizQuestion));

    when(mockQuizQuestion.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    when(quizQuestionRepository.save(mockQuizQuestion)).thenReturn(mockQuizQuestion);

    when(quizQuestionDtoMapper.apply(mockQuizQuestion)).thenReturn(mockQuizQuestionDto);

    // Then
    var result = underTest.updateQuizQuestion(quizQuestionId, updateRequest, mockAuthentication);

    assertNotNull(result);
    verify(mockQuizQuestion, times(1)).updateFromRequest(updateRequest);
  }

  @Test
  @DisplayName("It will throw when quiz question is not found when updating")
  void itWillThrowWhenQuizQuestionNotFoundWhenUpdateQuizQuestion() {
    // Given
    Long quizQuestionId = FAKER.random().nextLong(0, 100);
    QuizQuestion mockQuizQuestion = mock(QuizQuestion.class);
    Authentication mockAuthentication = mock(Authentication.class);
    QuizQuestionUpdateRequest updateRequest = new QuizQuestionUpdateRequest("new question");

    // When
    when(quizQuestionRepositoryCustom.findById(quizQuestionId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(
            () -> underTest.updateQuizQuestion(quizQuestionId, updateRequest, mockAuthentication))
        .isInstanceOf(QuizQuestionNotFoundException.class)
        .hasMessage("Quiz question with id '" + quizQuestionId + "' was not found");

    verify(mockQuizQuestion, never()).updateFromRequest(updateRequest);
    verify(quizQuestionRepository, never()).save(any());
  }

  @Test
  @DisplayName("It should delete quiz question")
  void itShouldDeleteQuizQuestion() {
    // Given
    Long quizQuestionId = FAKER.random().nextLong(0, 100);
    QuizQuestion mockQuizQuestion = mock(QuizQuestion.class);

    Authentication mockAuthentication = mock(Authentication.class);

    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizQuestionRepositoryCustom.findById(quizQuestionId))
        .thenReturn(Optional.of(mockQuizQuestion));

    when(mockQuizQuestion.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    // Then
    var result = underTest.deleteQuizQuestion(quizQuestionId, mockAuthentication);

    assertEquals(quizQuestionId, result);
    verify(quizQuestionRepositoryCustom, times(1)).delete(any());
  }

  @Test
  @DisplayName("It will throw when quiz question not found when deleting")
  void itWillThrowWhenQuizQuestionNotFoundWhenDeleteQuizQuestion() {
    // Given
    Long quizQuestionId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizQuestionRepositoryCustom.findById(quizQuestionId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> underTest.deleteQuizQuestion(quizQuestionId, mockAuthentication))
        .isInstanceOf(QuizQuestionNotFoundException.class)
        .hasMessage("Quiz question with id '" + quizQuestionId + "' was not found");

    verify(quizQuestionRepository, never()).delete(any());
  }
}
