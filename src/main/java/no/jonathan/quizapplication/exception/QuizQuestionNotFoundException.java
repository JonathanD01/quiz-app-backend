package no.jonathan.quizapplication.exception;

public class QuizQuestionNotFoundException extends RuntimeException {

  public QuizQuestionNotFoundException(Long quizQuestionId) {
    super("Quiz question with id '" + quizQuestionId + "' was not found");
  }
}
