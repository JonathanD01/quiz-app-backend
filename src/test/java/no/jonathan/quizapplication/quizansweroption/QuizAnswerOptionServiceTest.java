package no.jonathan.quizapplication.quizansweroption;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import net.datafaker.Faker;
import no.jonathan.quizapplication.exception.QuizAnswerOptionNotFoundException;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.quizquestion.QuizQuestionModuleApi;
import no.jonathan.quizapplication.shared.QuizAnswerOptionDtoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class QuizAnswerOptionServiceTest {

  private static final Faker FAKER = new Faker();

  @InjectMocks private QuizAnswerOptionService underTest;

  @Mock private QuizQuestionModuleApi quizQuestionModuleApi;
  @Mock private QuizAnswerOptionRepository quizAnswerOptionRepository;
  @Mock private QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper;

  @Test
  @DisplayName("It should create a quiz answer option")
  void itShouldCreateQuizAnswerOption() {
    // TODO mockStaticQuizAnswerOption is not used, but test wont pass without ?
    try (MockedStatic<QuizAnswerOption> mockStaticQuizAnswerOption =
        mockStatic(QuizAnswerOption.class)) {
      // Given
      Long quizQuestionId = FAKER.random().nextLong(0, 100);
      QuizQuestion mockQuizQuestion = mock(QuizQuestion.class);
      Authentication mockAuthentication = mock(Authentication.class);
      QuizAnswerOption mockQuizAnswerOption = mock(QuizAnswerOption.class);
      QuizAnswerOptionDto mockQuizAnswerOptionDto = mock(QuizAnswerOptionDto.class);
      QuizAnswerOptionCreateRequest createRequest =
          new QuizAnswerOptionCreateRequest(
              quizQuestionId, "random text", FAKER.random().nextBoolean());

      String entityCreatedByEmail = FAKER.internet().emailAddress();

      // When
      when(quizQuestionModuleApi.findById(quizQuestionId)).thenReturn(mockQuizQuestion);
      when(mockQuizQuestion.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

      when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

      when(QuizAnswerOption.fromCreateRequest(mockQuizQuestion, createRequest))
          .thenReturn(mockQuizAnswerOption);

      when(quizAnswerOptionRepository.save(mockQuizAnswerOption)).thenReturn(mockQuizAnswerOption);

      when(quizAnswerOptionDtoMapper.apply(mockQuizAnswerOption))
          .thenReturn(mockQuizAnswerOptionDto);

      // Then
      var result = underTest.createQuizAnswerOption(createRequest, mockAuthentication);

      assertNotNull(result);
      verify(quizAnswerOptionRepository, times(1)).save(mockQuizAnswerOption);
      verify(quizAnswerOptionDtoMapper, times(1)).apply(mockQuizAnswerOption);
    }
  }

  @Test
  @DisplayName("It should update quiz answer option")
  void itShouldUpdateQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);
    QuizAnswerOption mockQuizAnswerOption = mock(QuizAnswerOption.class);
    QuizAnswerOptionDto mockQuizAnswerOptionDto = mock(QuizAnswerOptionDto.class);
    QuizAnswerOptionUpdateRequest updateRequest =
        new QuizAnswerOptionUpdateRequest("new text", FAKER.random().nextBoolean());

    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizAnswerOptionRepository.findById(quizAnswerOptionId))
        .thenReturn(Optional.of(mockQuizAnswerOption));

    when(mockQuizAnswerOption.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    when(quizAnswerOptionRepository.save(mockQuizAnswerOption)).thenReturn(mockQuizAnswerOption);

    when(quizAnswerOptionDtoMapper.apply(mockQuizAnswerOption)).thenReturn(mockQuizAnswerOptionDto);

    // Then
    var result =
        underTest.updateQuizAnswerOption(quizAnswerOptionId, updateRequest, mockAuthentication);

    assertNotNull(result);
    verify(quizAnswerOptionRepository, times(1)).save(mockQuizAnswerOption);
    verify(quizAnswerOptionDtoMapper, times(1)).apply(mockQuizAnswerOption);
  }

  @Test
  @DisplayName("It will throw when quiz answer option is not found by id")
  void itWillThrowWhenQuizAnswerOptionNotFoundWhenUpdateQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);
    QuizAnswerOptionUpdateRequest updateRequest =
        new QuizAnswerOptionUpdateRequest("new text", FAKER.random().nextBoolean());

    // When
    when(quizAnswerOptionRepository.findById(quizAnswerOptionId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(
            () ->
                underTest.updateQuizAnswerOption(
                    quizAnswerOptionId, updateRequest, mockAuthentication))
        .isInstanceOf(QuizAnswerOptionNotFoundException.class)
        .hasMessage("Quiz answer option with id '" + quizAnswerOptionId + "' was not found");

    verify(quizAnswerOptionRepository, never()).save(any());
    verify(quizAnswerOptionDtoMapper, never()).apply(any());
  }

  @Test
  @DisplayName("It should delete quiz answer option")
  void itShouldDeleteQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);
    QuizAnswerOption mockQuizAnswerOption = mock(QuizAnswerOption.class);

    String entityCreatedByEmail = FAKER.internet().emailAddress();

    // When
    when(quizAnswerOptionRepository.findById(quizAnswerOptionId))
        .thenReturn(Optional.of(mockQuizAnswerOption));

    when(mockQuizAnswerOption.getCreatedByEmail()).thenReturn(entityCreatedByEmail);

    when(mockAuthentication.getName()).thenReturn(entityCreatedByEmail);

    // Then
    var result = underTest.deleteQuizAnswerOption(quizAnswerOptionId, mockAuthentication);

    assertEquals(quizAnswerOptionId, result);
    verify(quizAnswerOptionRepository, times(1)).delete(mockQuizAnswerOption);
  }

  @Test
  @DisplayName("It will throw when quiz answer option not found")
  void itWillThrowWhenQuizAnswerOptionNotFoundWhenQuizAnswerOption() {
    // Given
    Long quizAnswerOptionId = FAKER.random().nextLong(0, 100);
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(quizAnswerOptionRepository.findById(quizAnswerOptionId)).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(
            () -> underTest.deleteQuizAnswerOption(quizAnswerOptionId, mockAuthentication))
        .isInstanceOf(QuizAnswerOptionNotFoundException.class)
        .hasMessage("Quiz answer option with id '" + quizAnswerOptionId + "' was not found");

    verify(quizAnswerOptionRepository, never()).save(any());
  }
}
