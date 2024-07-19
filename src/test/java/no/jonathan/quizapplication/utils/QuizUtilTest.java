package no.jonathan.quizapplication.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.persistence.EntityManager;
import net.datafaker.Faker;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.shared.FakeQuizGeneratorBot;
import no.jonathan.quizapplication.shared.IQuizGeneratorBot;
import no.jonathan.quizapplication.shared.QuizFromAi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class QuizUtilTest {

  private static final Faker FAKER = new Faker();

  @InjectMocks private QuizUtil underTest;
  @Mock private QuizModuleApi quizModuleApi;
  @Mock private SimpMessagingTemplate simpMessagingTemplate;
  @Mock private IQuizGeneratorBot quizGeneratorBot;
  @Mock private EntityManager entityManager;

  @Test
  @DisplayName("It should finish the quiz creation")
  void itShouldContinueAndFinishQuizCreation() {
    // Given
    Quiz quiz = Quiz.createUnfinishedQuiz(null);
    QuizFromAi quizFromAi = new FakeQuizGeneratorBot().getQuizAiFromChatBot(null, null);
    String username = FAKER.internet().emailAddress();
    String language = "norwegian";
    byte[] bytes = {};

    // When
    when(quizGeneratorBot.getQuizAiFromChatBot(any(), any())).thenReturn(quizFromAi);

    when(quizModuleApi.save(quiz)).thenReturn(quiz);

    // Then
    underTest.continueAndFinishQuizCreation(quiz, username, language, bytes);

    verify(quizModuleApi, times(1)).save(quiz);
    verify(simpMessagingTemplate, times(1)).convertAndSendToUser(any(), any(), any());
    verify(entityManager, atLeast(2)).persist(any());
  }

  @Test
  @DisplayName("It should handle error from Quiz#createUnfinishedQuiz when finishing quiz creation")
  void itShouldHandleErrorWhenContinueAndFinishQuizCreation() {
    // Given
    Quiz quiz = Quiz.createUnfinishedQuiz(null);
    String username = FAKER.internet().emailAddress();
    String language = "french";
    byte[] bytes = {};

    // When
    when(quizModuleApi.save(quiz)).thenReturn(quiz);

    // Then
    underTest.continueAndFinishQuizCreation(quiz, username, language, bytes);

    verify(quizModuleApi, times(1)).save(quiz);
    verify(simpMessagingTemplate, times(2)).convertAndSendToUser(any(), any(), any());
  }
}
