package no.jonathan.quizapplication.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.IOException;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.quiz.Status;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOption;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.shared.*;
import no.jonathan.quizapplication.websocket.WebSocketMessageResponse;
import no.jonathan.quizapplication.websocket.WebSocketQuizResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class QuizUtil {

  private final QuizModuleApi quizModuleApi;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private final IQuizGeneratorBot quizGeneratorBot;

  @PersistenceContext
  private final EntityManager entityManager;

  public QuizUtil(
          QuizModuleApi quizModuleApi,
          SimpMessagingTemplate simpMessagingTemplate,
          IQuizGeneratorBot quizGeneratorBot,
          EntityManager entityManager) {
    this.quizModuleApi = quizModuleApi;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.quizGeneratorBot = quizGeneratorBot;
    this.entityManager = entityManager;
  }

  @Transactional
  @Async
  public void continueAndFinishQuizCreation(
          Quiz unfinishedQuiz, String username, String language, byte[] bytes) {
    QuizFromAi quizFromAi = quizGeneratorBot.getQuizAiFromChatBot(language, new ByteArrayResource(bytes));

    // Stop here to stop test from breaking!
    if (quizGeneratorBot instanceof FakeQuizGeneratorBot) {
      return;
    }

    if (quizFromAi == null) {
      handleError(unfinishedQuiz, username, null);
      return;
    }

    // Create a new quiz with the provided creator and default name
    unfinishedQuiz.setTitle(quizFromAi.quizTitle());
    unfinishedQuiz.setDescription(quizFromAi.quizDescription());

    // Iterate over each question received from the AI
    for (QuizQuestionFromAi quizQuestionFromAi : quizFromAi.questionByAiList()) {
      // Create a new quiz question based on AI-provided text
      QuizQuestion newQuizQuestion = QuizQuestion.create(quizQuestionFromAi.questionText());
      newQuizQuestion.linkToQuiz(unfinishedQuiz);
      newQuizQuestion.setCreatedBy(username);

      // Save quiz question
      entityManager.persist(newQuizQuestion);

      // Iterate over each answer option for the current question from AI
      for (QuizAnswerOptionFromAi quizAnswerOptionFromAi :
              quizQuestionFromAi.quizAnswerOptionFromAis()) {
        // Retrieve answer text and correctness flag from AI data
        String quizAnswerOptionText = quizAnswerOptionFromAi.answerOptionText();
        boolean isQuizAnswerCorrect = quizAnswerOptionFromAi.correct();

        var newQuizAnswerOption =
                QuizAnswerOption.create(quizAnswerOptionText, isQuizAnswerCorrect);
        newQuizAnswerOption.setCreatedBy(username);
        newQuizAnswerOption.linkToQuestion(newQuizQuestion);

        // save quiz answer options
        entityManager.persist(newQuizAnswerOption);

        // Create a new answer option and add it to the question
        newQuizQuestion.addAnswerOption(newQuizAnswerOption);
      }
      // Add the created question to the quiz
      unfinishedQuiz.addQuestion(newQuizQuestion);
    }

    // Update status
    unfinishedQuiz.updateStatus(Status.COMPLETED);

    // Save quiz
    var finishedQuiz = quizModuleApi.save(unfinishedQuiz);

    var responseObject =
            new WebSocketQuizResponse(
                    finishedQuiz.getId(),
                    finishedQuiz.getStatus().toString(),
                    finishedQuiz.getTitle(),
                    finishedQuiz.getDescription());

    try {
      var response = new ObjectMapper().writeValueAsString(responseObject);

      // Send to Websocket
      simpMessagingTemplate.convertAndSendToUser(username, "topic/quiz", response);
    } catch (IOException e) {
      handleError(unfinishedQuiz, username, e);
    }
  }

  private void handleError(Quiz unfinishedQuiz, String username, Exception e) {
    unfinishedQuiz.updateStatus(Status.FAILED);

    unfinishedQuiz = quizModuleApi.save(unfinishedQuiz);

    var failedResponseObject =
            new WebSocketQuizResponse(
                    unfinishedQuiz.getId(),
                    unfinishedQuiz.getStatus().toString(),
                    unfinishedQuiz.getTitle(),
                    unfinishedQuiz.getDescription());

    var failedMessageResponse =
            new WebSocketMessageResponse(
                    "Error", "Quiz creation failed. Please delete it and try again");

    // Send to Websocket
    simpMessagingTemplate.convertAndSendToUser(username, "topic/quiz", failedResponseObject);
    simpMessagingTemplate.convertAndSendToUser(username, "topic/quiz", failedMessageResponse);

    if (e != null) {
      // TODO Log better
      e.printStackTrace();
    }
  }
}
